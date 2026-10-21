package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.Raffle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointsMapper {
    //抽奖
    void raffle(int reward, Long currentUserId);

    //检查积余额
    Integer check(Long currentUserId);
}
