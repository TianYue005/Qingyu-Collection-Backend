package com.wang.tradingplatform;

import com.wang.tradingplatform.properties.AliyunOSSProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy//开启SpringAOP
@EnableTransactionManagement
@EnableConfigurationProperties(AliyunOSSProperties.class)
@MapperScan("com.wang.tradingplatform.mapper")
public class TradingPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingPlatformApplication.class, args);
    }
}
