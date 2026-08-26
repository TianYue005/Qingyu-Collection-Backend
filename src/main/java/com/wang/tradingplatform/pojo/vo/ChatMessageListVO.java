package com.wang.tradingplatform.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageListVO {
    //主键自增id                    后端设置
    private Long id;
    //对方用户id：前端点开会话、发消息时需要（对应 ChatMessageListVO.toUid）
    private Long userId;
    //会话id                      后端设置
    private Long sessionId;
    //发送人uid                    前端传递
    private Long fromUid;
    //接收用户id，群聊为0             前端传递
    private Long toUid;
    //群id，私聊为0                  前端传递
    private Long groupId;
    //消息内容                      前端传递
    private String content;
    //消息类型 1 字符串 2 url链接     前端传递
    private Integer msgType;
    //开始时间  这个是整个会话最早的开始时间  后端设定
    private LocalDateTime startTime;
    //发送时间  这个是每一次对话的发送时间 后端设定
    private LocalDateTime sendTime;
    //聊天列表的头像（私聊的话是对方的头像，群聊是群聊的头像）
    private String avatar;
    //对方的用户名
    private String userName;
}
