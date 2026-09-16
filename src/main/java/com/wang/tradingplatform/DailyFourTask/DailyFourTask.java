package com.wang.tradingplatform.DailyFourTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyFourTask {
    private final RedisTemplate<String, Object> redisTemplate;

    //期望的like:circle:{circle} score=xxx
    //点赞增加50 评论增加100 每小时减10
    private final String REDIS_HOT_SORT_KEY = "like:circle";//临时热度榜单的前半部分key
    private final String REDIS_HOT_SORT_KEY_FINAL = "like:cirle:final";//最终热度榜单的前半部分key


    // 每天凌晨3点整执行 将临时的热度榜单score小于等于0的删掉
    @Scheduled(cron = "0 0 3 * * ?")
    public void RankingListCut() {
        try {
            Long removeCount = redisTemplate.opsForZSet().removeRangeByScore(REDIS_HOT_SORT_KEY, Double.NEGATIVE_INFINITY, 0);
            log.info("去掉了" + removeCount + "条信息");
        } catch (Exception e) {
            // 捕获异常并记录日志，避免定时任务因异常中断
            log.error("将临时的热度榜单score小于等于0的删掉 的定时任务出现错误", e);
        }
    }

    //每小时执行一次
    //先将临时榜单的所有score下降10 再从临时榜单的最高的10条同步到最终榜单
    @Scheduled(cron = "0 0 * * * ?")
    public void ListSynchronisation() {
        //先将临时榜单的所有score下降10
        try {
            decrAllScoreBy10(REDIS_HOT_SORT_KEY);
        } catch (Exception e) {
            throw new RuntimeException("将临时榜单的所有score下降10 出现错误！错误：" + e);
        }
        //同步
        try {
            Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(REDIS_HOT_SORT_KEY, 0, 9);
            //删除该key下的所有数据
            redisTemplate.opsForZSet().removeRange(REDIS_HOT_SORT_KEY_FINAL, 0, -1);
            if (tuples != null) {
                for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                    if (tuple == null) {
                        continue;
                    }
                    Object value = tuple.getValue();
                    Double score = tuple.getScore();
                    // ZSet 的 member 和 score 均不允许为 null，为 null 时跳过该条数据
                    if (value == null || score == null) {
                        continue;
                    }
                    // 只同步热度大于0的成员，负分或0分成员不再进入最终榜单
                    if (score <= 0) {
                        continue;
                    }
                    redisTemplate.opsForZSet().add(REDIS_HOT_SORT_KEY_FINAL, value, score);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("同步到最终榜单 出现错误！错误：" + e);
        }

    }


    /**
     * zset内所有成员score统一减去10
     *
     * @param key zset的key
     */
    public void decrAllScoreBy10(String key) {
        // range 0 -1 获取全部member，不要分数
        Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
        if (members == null || members.isEmpty()) {
            return;
        }
        for (Object member : members) {
            // ZINCRBY key -10 member ：每个成员分数-10
            redisTemplate.opsForZSet().incrementScore(key, member, -10);
        }
    }


}





