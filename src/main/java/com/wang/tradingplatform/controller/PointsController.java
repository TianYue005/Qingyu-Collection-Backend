package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "积分中心")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointsController {
    //抽奖
    @Operation(summary = "抽奖")
    @GetMapping("/Raffle")
    public Result<Object> Raffle(){

        return Result.success();
    }
}
