package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.entity.Raffle;

public interface PointsService {
    //抽奖
    Raffle raffle();

    //状态检查
    Raffle check();
}
