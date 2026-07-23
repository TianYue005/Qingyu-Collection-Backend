package com.wang.tradingplatform.pojo.vo;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatListVO {
    //这条记录的id（自己的id）
    private Long id;
    //关联聊天的id
    private Long sessionId;
    //发送用户的id
    private Long fromUid;
    //接受用户的id
    private Long toUid;
    //聊天列表展示的图片（群聊图片/对方头像）
    private String avatar;
    //聊天题目(对方的用户名/群聊名字)
    private String title;
    //发送时间
    private String sendTime;
    //发送人昵称
    private String nickName;
    //最后一条消息内容
    private String content;
}
