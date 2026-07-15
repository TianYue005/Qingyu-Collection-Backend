package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.services.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 群聊
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ChatMessage sendGroupMessage(@DestinationVariable String roomId, ChatMessage message) {
        return message; // 自动广播给订阅了该房间的所有用户
    }

    // 私聊  
    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(Principal principal, ChatMessage message) {
        // message.getToUid() 包含了目标用户名
        // 消息将被发送至 /user/{toUser}/queue/private
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getToUid()),
                "/queue/private",
                message
        );
    }
}
