package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.Picture;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.utils.AliyunOSSOperator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "图片上传模块")
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;


    //上传图片
    @PostMapping()
    public Result<List<Picture>> upload(MultipartFile[] files) throws Exception {
        if (files == null || files.length == 0) {
            return Result.success(Collections.emptyList());
        }

        Picture[] pictures = new Picture[files.length];              // 按索引存放结果
        List<CompletableFuture<Void>> futures = new ArrayList<>();   // 存放所有异步任务

        for (int i = 0; i < files.length; i++) {
            final int index = i;                                     // 必须用 final 变量
            MultipartFile file = files[i];

            // 过滤掉空文件，防止后续报错
            if (file.isEmpty()) {
                continue;
            }

            // 1. 主线程读取文件内容和尺寸
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();

            // 尝试解析图片
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));

            // 获取图片的宽高，如果 image 为 null 则给个默认值（或者你可以选择抛出异常）
            int width = (image != null) ? image.getWidth() : 0;
            int height = (image != null) ? image.getHeight() : 0;

            // 2. 提交异步上传任务（不阻塞，立刻继续下一张）
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String url = aliyunOSSOperator.upload(
                            new ByteArrayInputStream(fileBytes),
                            Objects.requireNonNull(originalFilename)
                    );
                    log.info("文件上传到OSS的url {} ", url);

                    // 将获取到的宽高传入
                    pictures[index] = new Picture(url, width, height);
                } catch (Exception e) {
                    log.error("文件上传失败: index={}, filename={}", index, originalFilename, e);
                    throw new RuntimeException("文件上传失败: " + originalFilename, e);
                }
            });
            futures.add(future);
        }

        // 3. 等待所有上传任务全部完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 过滤掉因为 file.isEmpty() 导致数组里可能存在的 null 元素
        List<Picture> resultList = Arrays.stream(pictures)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Result.success(resultList);
    }
}
