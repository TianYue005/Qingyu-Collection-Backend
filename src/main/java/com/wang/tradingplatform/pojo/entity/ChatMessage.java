package com.wang.tradingplatform.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    //主键自增id
    private Long id;
    //会话id
    private String sessionId;
    //发送人uid #
    private Long fromUid;
    //接收用户id，群聊为0  #
    private Long toUid;
    //群id，私聊为0 #
    private Long groupId;
    //消息内容 #
    private String content;
    //消息类型 1 字符串 2 url链接 #
    private Integer msgType;
    //发送时间
    private LocalDateTime sendTime;
    //是否已读：0未读 1已读
    private Integer isRead;
}
