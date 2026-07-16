package com.wang.tradingplatform.services;


import com.wang.tradingplatform.pojo.entity.ChatMessage;

import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessage message);

    List<ChatMessage> getHistoryByRoomId(String roomId, int limit);

    List<ChatMessage> getPrivateHistory(Long userId1, Long userId2, int limit);
}
