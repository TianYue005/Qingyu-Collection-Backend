package com.wang.tradingplatform.services;


import com.wang.tradingplatform.pojo.entity.ChatMessage;

import java.util.List;

public interface ChatService {
    //保存聊天信息到Redis与MySQL
    void saveMessage(ChatMessage webSocketDTO);

    List<ChatMessage> getPrivateHistory(Long userId1, Long userId2, int limit);
}
