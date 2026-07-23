package com.wang.tradingplatform.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String EXCHANGE_NAME = "chat.exchange";
    // 队列名称
    public static final String QUEUE_NAME = "chat.mysql.queue";
    // 绑定键  （在发送的时候就是路由键  在配置类里面设置就是绑定键）
    public static final String ROUTING_KEY = "chat.save.mysql";

    /**
     * 声明持久化的 Topic 交换机
     */
    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * 声明持久化队列
     */
    @Bean
    public Queue chatMysqlQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding chatMysqlBinding() {
        return BindingBuilder
                .bind(chatMysqlQueue())
                .to(chatExchange())
                .with(ROUTING_KEY);
    }
}
