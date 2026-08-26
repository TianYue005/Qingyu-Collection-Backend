package com.wang.tradingplatform.pojo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SessionState {
    private Long sessionId;//Session（对话）的id
    private Long isRead;//是否已读,读过为0，未读则未未读的用户的id
    private Long fromUid;//发送者id
    private Long toUid;//接收者id
    private Long groupId;//群聊id（私聊为0）
    private LocalDateTime createTime;//会话创建时间
}
