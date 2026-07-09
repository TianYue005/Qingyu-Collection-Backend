package com.wang.tradingplatform.DailyFourTask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 交给Spring管理，必不可少
public class DailyFourTask {

    // 每天凌晨4点整执行
    @Scheduled(cron = "${task.daily-four-cron}")
    public void runTask() {
        try {
            log.info("凌晨4点定时任务开始执行");
            // ====================业务操作====================
            log.info("凌晨4点定时任务执行完成");
        } catch (Exception e) {
            // 捕获异常，防止任务报错后次日不再执行
            log.error("凌晨4点定时任务执行异常", e);
        }
    }
}
