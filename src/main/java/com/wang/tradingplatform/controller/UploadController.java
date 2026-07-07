package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.utils.AliyunOSSOperator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Tag(name="图片上传模块")
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;


    //上传图片
    @PostMapping()
    public Result upload(MultipartFile[] files) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            log.info("文件上传{}", file.getOriginalFilename());
            String url = aliyunOSSOperator.upload(file.getInputStream(), file.getOriginalFilename());
            log.info("文件上传到OSS的url {} ", url);
            urls.add(url);
        }
        return Result.success(urls);
    }
}
