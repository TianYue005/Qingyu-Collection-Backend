package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.Raffle;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.impl.PointsServiceImpl;
import com.wang.tradingplatform.utils.UserContext;
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

    private final PointsServiceImpl pointsService;


    //抽奖
    @Operation(summary = "抽奖")
    @GetMapping("/Raffle")
    public Result<Raffle> Raffle() {
        Raffle raffle = pointsService.raffle();
        return Result.success(raffle);
    }

    //状态检查
    @Operation(summary = "状态检查")
    @GetMapping("/check")
    public Result<Raffle> check() {
        Raffle raffle = pointsService.check();
        return Result.success(raffle);
    }

    //为自己的商品购买热度



    //为自己的商品购买
}
