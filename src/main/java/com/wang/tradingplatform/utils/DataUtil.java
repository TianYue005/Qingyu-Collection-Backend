package com.wang.tradingplatform.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class DataUtil {
    private static final  Random random = new Random();



    //得到明天刚开始的时间即00：01  以String形式返回 格式XXXX-XX-XX (这一次的时间)
    public static String thisTimeString() {
        return LocalDateTime.now(ZoneId.of("Asia/Shanghai")).plusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    //得到现在距离明天的00：01还有多少秒钟，以Long类型返回
    public static Long distanceOfTomorrowSecond() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        LocalDateTime tomorrowStart = LocalDateTime
                .of(LocalDate.now(ZoneId.of("Asia/Shanghai")).plusDays(1), LocalTime.of(0, 1));
        return ChronoUnit.SECONDS.between(now, tomorrowStart);
    }

    //生成一个0-300（0-5分钟）
    public static Integer RandomTime5Minuit() {
        return random.nextInt(5001);
    }
}
