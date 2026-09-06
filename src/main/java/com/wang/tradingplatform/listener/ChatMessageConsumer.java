package com.wang.tradingplatform.listener;

import com.wang.tradingplatform.config.RabbitMQConfig;
import com.wang.tradingplatform.mapper.ChatMessageMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;

    /**
     * 监听了队列  QUEUE_NAME = "chat.mysql.queue" 一旦有消息就会自动运行
     *
     * @param message
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessageSave(ChatMessage message) {//形参由mq发送的message对象接收
        //点对点聊天的情况
        if (message.getGroupId() == null || message.getGroupId() == 0) {
            try {
                chatMessageMapper.insert(message);//向mysql中插入消息
                log.info("消息落库成功: id={}", message.getId());
            } catch (Exception e) {
                log.error("消息落库失败: id={}, error={}", message.getId(), e.getMessage());
            }
        } else {
            //TODO
            System.out.println("群聊相关功能为完善");
        }
    }

    //将交易请求同步到mysql（同步到trade_transaction表）
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME_TWO)
    public void tradeRequestSave(ChatMessage message) {
        try {
            userMapper.saveTradeRequest(message);
            log.info("消息落库成功: id={}", message.getId());
        } catch (Exception e) {
            log.error("消息落库失败: id={}, error={}", message.getId(), e.getMessage());
        }
    }
}
