package com.wang.tradingplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class Lua2RedisTemplate {

    //将Lua脚本从文件中读取 把脚本注册成 Bean，直接注入
    @Bean
    public DefaultRedisScript<Long> initDataScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("script/RecommendedSpot.lua"));
        script.setResultType(Long.class);
        return script;
    }

}
