package com.wang.tradingplatform.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RedissonConfig {
    // 从yml读取变量，:后面是默认值，不存在时使用
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;


    // todo  这里单机与集群是不一样的,以后在集群上不能用这个  redis/useClusterServers
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单机redis，集群用useClusterServers
        // 密码已从 SingleServerConfig 上移到 Config 级别（Redisson 4.x 起）
        config.setPassword(password);
        config.useSingleServer()
                .setAddress("redis://"+host+":"+port)
                .setDatabase(database);
        return Redisson.create(config);
    }
}
