package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.UserFeedback;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.UserFeedBackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户反馈模块")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedBack")
public class FeedBackController {
    private final UserFeedBackService feedBackService;


    //用户反馈
    @Operation(summary = "用户反馈")
    @PostMapping("/add")
    public Result<Object> feedBack(@RequestBody UserFeedback feedback) {
        Boolean b = feedBackService.addFeedBack(feedback);
        if (b){
            return Result.success();
        }
        return Result.error("反馈提交失败");
    }
}
