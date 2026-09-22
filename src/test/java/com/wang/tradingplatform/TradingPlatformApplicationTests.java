package com.wang.tradingplatform;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 应用启动冒烟测试：只验证 Spring 容器能否正常装配，不依赖任何真实的外部服务。
 * <p>
 * 说明：
 * 1. {@code VectorStore}（Qdrant 向量库）用 {@code @MockitoBean} 替换成 Mock，
 *    避免启动时因 Qdrant 未启动（localhost:6334 连接被拒绝）导致装配失败；
 * 2. 关闭 RabbitMQ 的队列自动声明与监听器自动启动，避免测试时因 RabbitMQ 未启动而失败。
 */
@SpringBootTest(properties = {
        "spring.rabbitmq.dynamic=false",
        "spring.rabbitmq.listener.simple.auto-startup=false"
})
class TradingPlatformApplicationTests {

    @MockitoBean
    private VectorStore vectorStore;

    @Test
    void contextLoads() {
    }

}
