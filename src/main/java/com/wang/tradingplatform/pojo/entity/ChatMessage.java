package com.wang.tradingplatform.pojo.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    //主键自增id                    后端设置
    private Long id;
    //会话id                      后端设置
    private String sessionId;
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
    //发送时间                      后端设定
    private LocalDateTime sendTime;
    //是否已读：0未读 1已读           前端传递
    private Integer isRead;
    /*//话题 将来用来提醒交易双方正在讨论什么
    private String topic;*/
}
