package com.wang.tradingplatform;

import com.wang.tradingplatform.properties.AliyunOSSProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties(AliyunOSSProperties.class)
@MapperScan("com.wang.tradingplatform.mapper")
public class TradingPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingPlatformApplication.class, args);
    }
}
