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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
        Picture[] pictures = new Picture[files.length];              // 按索引存放结果
        List<CompletableFuture<Void>> futures = new ArrayList<>();   // 存放所有异步任务

        for (int i = 0; i < files.length; i++) {
            final int index = i;                                     // 必须用 final 变量
            MultipartFile file = files[i];

            // 1. 主线程读取文件内容和尺寸（很快）
            byte[] fileBytes = file.getBytes();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            String originalFilename = file.getOriginalFilename();

            // 2. 提交异步上传任务（不阻塞，立刻继续下一张）
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    String url = aliyunOSSOperator.upload(
                            new ByteArrayInputStream(fileBytes),
                            Objects.requireNonNull(originalFilename)
                    );
                    log.info("文件上传到OSS的url {} ", url);
                    pictures[index] = new Picture(url, image.getWidth(), image.getHeight());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }

        // 3. 等待所有上传任务全部完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return Result.success(Arrays.asList(pictures));
    }
}
