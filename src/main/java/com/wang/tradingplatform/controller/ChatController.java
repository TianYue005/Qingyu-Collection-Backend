package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.WebSocket.WebSocketSessionManager;
import com.wang.tradingplatform.pojo.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Controller
public class ChatController {

    // 日志
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    public ChatController(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager) {
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    // ========== 公共聊天（广播） ==========
    // 前端发送消息到 /app/publicChat
    // 服务端处理后，自动广播到所有订阅了 /topic/public 的客户端
    @MessageMapping("/publicChat")
    @SendTo("/topic/public")
    public ChatMessage publicChat(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 从握手时存入的 session 属性中获取当前用户
        Long senderId = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");
        message.setFromUid(senderId);
        message.setSendTime(LocalDateTime.now());
        return message;
    }

    // ========== 点对点私聊 ==========
    // 前端发送消息到 /app/privateChat
    // 接收方订阅 /queue/chat.{自己的userId} 即可收到
    @MessageMapping("/privateChat")
    public void privateChat(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 从握手时存入的 session 属性中获取当前用户  
        Long senderId = (Long) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");
        message.setFromUid(senderId);
        message.setSendTime(LocalDateTime.now());
        message.setGroupId(0L);  // 私聊，groupId 为 0

        Long toUid = message.getToUid();
        if (toUid == null) {
            log.warn("私聊消息缺少接收者 toUid");
            return;
        }

        // 检查接收者是否在线
        Set<String> targetSessions = sessionManager.getSessionIdsByUserId(toUid);
        if (targetSessions.isEmpty()) {
            // 对方不在线，可以通知发送者
            log.info("用户 {} 不在线，消息暂存", toUid);
            ChatMessage offlineNotice = new ChatMessage();
            offlineNotice.setContent("对方不在线");
            offlineNotice.setMsgType(3); // 系统提示
            offlineNotice.setSendTime(LocalDateTime.now());
            messagingTemplate.convertAndSend("/queue/chat." + senderId, offlineNotice);
            return;
        }

        // 发送给接收方：投递到 /queue/chat.{toUid}
        messagingTemplate.convertAndSend("/queue/chat." + toUid, message);
        log.info("私聊消息: {} -> {}, 内容: {}", senderId, toUid, message.getContent());

        // 同时回传一份给发送方（多端同步确认）
        messagingTemplate.convertAndSend("/queue/chat." + senderId, message);
    }

    // ========== 服务端主动推送系统通知 ==========
    public void sendSystemNotice(String content) {
        ChatMessage notice = new ChatMessage();
        notice.setContent(content);
        notice.setMsgType(3); // 3 = 系统消息
        notice.setSendTime(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/notice", notice);
    }
}
