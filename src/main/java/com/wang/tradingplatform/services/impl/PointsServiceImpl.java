package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.mapper.PointsMapper;
import com.wang.tradingplatform.pojo.entity.Raffle;
import com.wang.tradingplatform.services.PointsService;
import com.wang.tradingplatform.utils.DataUtil;
import com.wang.tradingplatform.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final PointsMapper pointsMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    //抽奖

    /**
     * 奖励有 20 30 40 50 60 70 80 90 100 200
     * 其中 20~100 各占 11% 概率，200 分大奖占 1% 概率
     *
     * @return 抽奖结果
     */
    @Override
    @Transactional
    public Raffle raffle() {
        if (redisTemplate.opsForValue().get("raffle:" + UserContext.getCurrentUserId() + ":" + DataUtil.thisTimeString()) != null) {
            //说明里面有存储这个key，该用户今天有抽过奖
            throw new RuntimeException("今天已经抽过奖了~");
        }
        int r = ThreadLocalRandom.current().nextInt(100); // 0 ~ 99
        // 99 命中 200 分大奖（1% 概率）
        if (r == 99) {
            return new Raffle("恭喜获得 200 积分", 200);
        }
        // 其余 99 个数平均分给 9 档，每 11 个为一档
        int reward = 20 + (r / 11) * 10;
        try {
            pointsMapper.raffle(reward, UserContext.getCurrentUserId());
            redisTemplate.opsForValue()
                    .set("raffle:" + UserContext.getCurrentUserId() + ":" + DataUtil.thisTimeString(), "1",
                            DataUtil.distanceOfTomorrowSecond(), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("积分不足");
        }
        return new Raffle("恭喜获得 " + reward + " 积分", reward);
    }

    //状态检查
    @Override
    public Raffle check() {
        int point = pointsMapper.check(UserContext.getCurrentUserId());
        System.out.println(point);
        //得到现在还允许抽奖么
        if (redisTemplate.opsForValue().get("raffle:" + UserContext.getCurrentUserId() + ":" + DataUtil.thisTimeString()) != null) {
            //说明里面有存储这个key，该用户今天还有抽过奖
            return new Raffle(point,false, "今天已经抽过奖了~");
        }
        return new Raffle(point, true);
    }
}
