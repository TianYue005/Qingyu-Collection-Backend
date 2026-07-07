package com.wang.tradingplatform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSProperties {
    private String endpoint;
    private String bucketName;
    private String region;
    @Override
    public String toString() {
        return "AliyunOSSProperties{" +
                "bucketName='" + bucketName + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
