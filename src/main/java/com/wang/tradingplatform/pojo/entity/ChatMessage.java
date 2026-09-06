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
    private Long sessionId;
    //发送人uid                    前端传递
    private Long fromUid;
    //接收用户id，群聊为0             前端传递
    private Long toUid;
    //群id，私聊为0                  前端传递
    private Long groupId;
    //消息内容                      前端传递
    private String content;
    //消息类型 1 字符串 2 url链接（如果是链接直接请求链接的到图片数据）     前端传递
    //3为商品主题（即想要发送一个商品id） 4为发起交易请求  5为请求对方刷新已经请求过的商品交易状态信息
    //描述的是content的内容是什么
    private Integer msgType;
    //开始时间                      后端设定
    private LocalDateTime startTime;
    //发送时间                      后端设定
    private LocalDateTime sendTime;
    //是否已读
    private Integer isRead;//0为未读，1为已读
    //WebSocket专用变量  0为正常聊天 1为更新为已读状态
    private Integer type;
    //websocket专用变量 商品id
    private Long goodsId;
}
