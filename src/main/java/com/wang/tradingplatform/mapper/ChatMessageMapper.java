package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper {


    // 插入消息
    int insert(ChatMessage message);


    //TODO 代码内容还没有检查

    /**
     * 查询私聊历史消息（按时间倒序）
     */
    List<ChatMessage> selectPrivateHistory(Long userId1, Long userId2, int limit);
}
