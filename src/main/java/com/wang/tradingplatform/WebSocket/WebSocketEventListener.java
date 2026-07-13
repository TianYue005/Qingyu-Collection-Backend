package com.wang.tradingplatform.WebSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

/**
 * WebSocket 事件监听器
 * 监听 STOMP 连接/断开事件，维护在线用户列表
 */
@Slf4j
@Component
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;

    public WebSocketEventListener(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * STOMP 连接建立时触发
     * 从握手阶段存入的 session 属性中取出 userAccount，注册到会话管理器
     */
    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // 从 WebSocket session 属性中获取握手时存入的信息
        String userAccount = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userAccount");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String sessionId = headerAccessor.getSessionId();

        if (userAccount != null && userId != null && sessionId != null) {
            sessionManager.registerSession(userAccount, userId, sessionId);
            log.info("用户上线: userAccount={}, userId={}, sessionId={}, 当前在线: {}", 
                     userAccount, userId, sessionId, sessionManager.getOnlineCount());
        }
    }

    /**
     * STOMP 连接断开时触发
     */
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId != null) {
            sessionManager.removeSession(sessionId);
            log.info("用户下线: sessionId={}, 当前在线: {}", sessionId, sessionManager.getOnlineCount());
        }
    }
}
