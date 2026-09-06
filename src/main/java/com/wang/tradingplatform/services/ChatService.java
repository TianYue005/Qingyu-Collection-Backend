package com.wang.tradingplatform.services;


import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;

import java.util.List;

public interface ChatService {
    //保存聊天信息到Redis与MySQL
    void saveMessage(ChatMessage webSocketDTO);

    List<ChatMessage> getPrivateHistoryRedis(ItemQueryParam itemQueryParam);

}
