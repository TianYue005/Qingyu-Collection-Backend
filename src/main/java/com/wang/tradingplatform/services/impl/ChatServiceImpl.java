package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.config.RabbitMQConfig;
import com.wang.tradingplatform.mapper.ChatMessageMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.ProductAssociationVO;
import com.wang.tradingplatform.services.ChatService;
import com.wang.tradingplatform.utils.RedisUtil;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final SnowflakeIdUtil snowflakeIdUtil;
    private final RabbitTemplate rabbitTemplate;
    private final RedisUtil redisUtil;
    private final ChatMessageMapper chatMessageMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final UserMapper userMapper;

    //保存聊天信息到Redis与MySQL  todo --------------------------
    @Override
    public void saveMessage(ChatMessage message) {
        // 1. 生成唯一ID并补齐时间字段  目前两个id（主键id与会话id）是相同的
        long msgId = snowflakeIdUtil.nextId();
        if (message.getSessionId() == null) {
            throw new RuntimeException("没有传递sessionId");
        }
        message.setId(msgId);
        message.setSendTime(LocalDateTime.now());//发送时间
        message.setIsRead(0);//是否已读

        // 2. 投递到 RabbitMQ，由消费者异步写入 MySQL  这里的message应该是所有内容都包含的
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        // 3. 同步写入 Redis（当天热数据，极快）
        saveToRedis(message, message.getSessionId());

        if (message.getMsgType() == 4) {
            if (message.getGoodsId()==null||message.getSessionId()==null){
                return;
            }
            //说明是想发起一个交易请求
            //将交易请求同步写入redis与MySQL
            String redisKey = "TradeState:" + message.getSessionId() + message.getGoodsId();
            ProductAssociationVO vo = userMapper.selectTradeInfo(message.getGoodsId(), message.getSessionId());
            redisUtil.set(redisKey, vo);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_TWO,
                    message
            );

        }

    }


    /**
     * 得到存在redis的私聊消息
     *
     * @param itemQueryParam 需要传递sessionId pageNumber，pageSizemoren
     * @return
     */
    @Override
    public List<ChatMessage> getPrivateHistoryRedis(ItemQueryParam itemQueryParam) {
        //先生成key
        String key = buildPrivateRedisKey(itemQueryParam.getSessionId());
        int pageSize = itemQueryParam.getPageSize();
        int pageNumber = -itemQueryParam.getPageNumber() * 10;

        Set<Object> message = redisUtil.zRange(key, pageNumber, pageNumber + pageSize - 1);
        if (message == null || message.isEmpty()) {//如果是空内容就返回空集合
            return List.of();
        }
        return message.stream()
                .filter(ChatMessage.class::isInstance)
                .map(ChatMessage.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * 将消息追加到 Redis zSet，并设置过期时间为当7天后
     */
    private void saveToRedis(ChatMessage message, Long sessionId) {
        String key;
        if (message.getGroupId() == null || message.getGroupId() == 0) {
            //GroupId为0，则是私聊
            key = buildPrivateRedisKey(sessionId);//发送者id与接收者id
        } else {
            //否则是群聊
            key = buildGroupRedisKey(message.getGroupId(), sessionId);//颧髎代码未检查 todo
        }
        //存入redis的List列表 按照当前时间的时间戳作为score来排序
        redisUtil.zAdd(key, message, System.currentTimeMillis());
        //这里设置过期时间是通过  相同的key 来进行绑定的
        redisUtil.expire(key, 7, TimeUnit.DAYS);//设置过期时间  过期时间为今天晚上12点
    }

    /**
     * 从 Redis zSet 中读取消息（尾部最新 100 条，因为是右插入）
     */
    private List<ChatMessage> getMessagesFromRedis(String key) {
        Set<Object> message = redisUtil.zRange(key, -30, -1);//查询到最新的30条数据
        if (message == null || message.isEmpty()) {//如果是空内容就返回空集合
            return List.of();
        }
        return message.stream()
                .filter(ChatMessage.class::isInstance)
                .map(ChatMessage.class::cast)
                .collect(Collectors.toList());
    }

    // ==================== Key 生成 ====================

    /**
     * 群聊 Redis Key：chat:room:{groupId}:{yyyyMMdd}
     */
    private String buildGroupRedisKey(Long groupId, Long sessionId) {
        return "chat:room:" + groupId + ":" + LocalDate.now().format(DATE_FMT);
    }

    /**
     * 私聊 Redis Key：chat:private:{minUid}:{maxUid}:{yyyyMMdd}
     */
    private String buildPrivateRedisKey(Long sessionId) {
        return "chat:private:" + sessionId;
    }

    /**
     * 计算到当天 24:00 的剩余秒数
     */
    private long secondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        return ChronoUnit.SECONDS.between(now, midnight);
    }


}
