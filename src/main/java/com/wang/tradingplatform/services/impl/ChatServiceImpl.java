package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.services.ChatService;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final SnowflakeIdUtil snowflakeIdUtil;


    @Override
    public void saveMessage(ChatMessage message) {
        message.setSessionId(String.valueOf(snowflakeIdUtil.nextId()));
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(0);
        // TODO: 调用 Mapper 写入数据库
    }

    @Override
    public List<ChatMessage> getHistoryByRoomId(String roomId, int limit) {
        // TODO: 从数据库查询群聊历史
        return List.of();
    }

    @Override
    public List<ChatMessage> getPrivateHistory(Long userId1, Long userId2, int limit) {
        // TODO: 从数据库查询私聊历史
        return List.of();
    }
}
