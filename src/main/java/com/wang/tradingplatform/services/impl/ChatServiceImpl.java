package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.annotation.Permission;
import com.wang.tradingplatform.config.RabbitMQConfig;
import com.wang.tradingplatform.mapper.ChatMessageMapper;
import com.wang.tradingplatform.pojo.entity.ChatMessage;
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

    @Override
    @Permission
    public void saveMessage(ChatMessage message) {
        // 1. 生成唯一ID并补齐时间字段  目前两个id（主键id与会话id）是相同的
        long msgId = snowflakeIdUtil.nextId();
        message.setId(msgId);
        message.setSessionId(String.valueOf(msgId));
        message.setSendTime(LocalDateTime.now());//发送时间
        message.setIsRead(0);//是否已读

        // 2. 投递到 RabbitMQ，由消费者异步写入 MySQL  这里的message应该是所有内容都包含的
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        // 3. 同步写入 Redis（当天热数据，极快）
        saveToRedis(message);
    }

    // ==================== 查询历史 ====================

    //todo 未检查的代码
    @Override
    @Permission
    public List<ChatMessage> getHistoryByRoomId(String roomId, int limit) {
        Long groupId = Long.valueOf(roomId);
        String todayKey = buildGroupRedisKey(groupId);

        // 先从 Redis 取当天消息
        List<ChatMessage> redisMessages = getMessagesFromRedis(todayKey);
        if (redisMessages.size() >= limit) {
            return redisMessages.subList(0, limit);
        }

        // Redis 不够，从 MySQL 补
        int remaining = limit - redisMessages.size();
        List<ChatMessage> dbMessages = chatMessageMapper.selectGroupHistory(groupId, remaining);

        List<ChatMessage> result = new ArrayList<>(redisMessages);
        result.addAll(dbMessages);
        return result;
    }

    //todo 未检查的代码
    @Override
    @Permission
    public List<ChatMessage> getPrivateHistory(Long userId1, Long userId2, int limit) {
        String todayKey = buildPrivateRedisKey(userId1, userId2);

        // 先从 Redis 取当天消息
        List<ChatMessage> redisMessages = getMessagesFromRedis(todayKey);
        if (redisMessages.size() >= limit) {
            return redisMessages.subList(0, limit);
        }

        // Redis 不够，从 MySQL 补
        int remaining = limit - redisMessages.size();
        List<ChatMessage> dbMessages = chatMessageMapper.selectPrivateHistory(userId1, userId2, remaining);

        List<ChatMessage> result = new ArrayList<>(redisMessages);
        result.addAll(dbMessages);
        return result;
    }

    /**
     * 将消息追加到 Redis zSet，并设置过期时间为当天 24:00
     */
    private void saveToRedis(ChatMessage message) {
        String key;
        if (message.getGroupId() == 0) {
            //GroupId为0，则是私聊
            key = buildPrivateRedisKey(message.getFromUid(), message.getToUid());//发送者id与接收者id
        } else {
            //否则是群聊
            key = buildGroupRedisKey(message.getGroupId());
        }
        //存入redis的List列表 按照当前时间的时间戳作为score来排序
        redisUtil.zAdd(key, message, System.currentTimeMillis());
        //这里设置过期时间是通过  相同的key 来进行绑定的
        redisUtil.expire(key, secondsUntilMidnight(), TimeUnit.SECONDS);//设置过期时间  过期时间为今天晚上12点
    }

    /**
     * 从 Redis zSet 中读取消息（尾部最新 100 条，因为是右插入）
     */
    private List<ChatMessage> getMessagesFromRedis(String key) {
        Set<Object> message  = redisUtil.zRange(key, -100, -1);//查询到最新的100条数据
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
    private String buildGroupRedisKey(Long groupId) {
        return "chat:room:" + groupId + ":" + LocalDate.now().format(DATE_FMT);
    }

    /**
     * 私聊 Redis Key：chat:private:{minUid}:{maxUid}:{yyyyMMdd}
     */
    private String buildPrivateRedisKey(Long uid1, Long uid2) {
        long minUid = Math.min(uid1, uid2);
        long maxUid = Math.max(uid1, uid2);
        return "chat:private:" + minUid + ":" + maxUid + ":" + LocalDate.now().format(DATE_FMT);
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
