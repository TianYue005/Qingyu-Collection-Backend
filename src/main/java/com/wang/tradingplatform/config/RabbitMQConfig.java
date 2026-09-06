package com.wang.tradingplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // 交换机名称
    public static final String EXCHANGE_NAME = "chat.exchange";
    //队列 一  存储聊天信息的队列
    // 队列名称
    public static final String QUEUE_NAME = "chat.mysql.queue";
    // 绑定键  （在发送的时候就是路由键  在配置类里面设置就是绑定键）
    public static final String ROUTING_KEY = "chat.save.mysql";

    //交换机 二 存储交易请求的队列
    public static final String QUEUE_NAME_TWO = "trade.request.queue";
    //绑定键
    public static final String ROUTING_KEY_TWO = "trade.save.mysql";

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

    @Bean
    public Queue saveTradeQueue() {
        return new Queue(QUEUE_NAME_TWO, true);
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

    @Bean
    public Binding tradeSaveBinding() {
        return BindingBuilder
                .bind(saveTradeQueue())
                .to(chatExchange())
                .with(ROUTING_KEY_TWO);
    }

    /**
     * 使用 JSON 序列化替代默认的 Java 序列化，
     * 避免消费端因反序列化白名单导致的 SecurityException。
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
