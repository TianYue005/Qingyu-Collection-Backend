package com.wang.tradingplatform.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 全局配置
 * 核心目的：解决 JavaScript Number 精度不足导致 Snowflake Long ID 丢失精度的问题。
 * 确保前端接收到的 ID 不会精度丢失。
 */
@Configuration
public class JacksonConfig {

    /**
     * 注册 Jackson Module，全局将 Long 类型序列化为字符串
     */
    @Bean
    public Module longToStringModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(long.class, ToStringSerializer.instance);
        return module;
    }
}
