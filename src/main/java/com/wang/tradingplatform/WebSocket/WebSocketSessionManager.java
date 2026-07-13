package com.wang.tradingplatform.WebSocket;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器
 * 维护 userId / userAccount → sessionId 的映射，实现点对点消息的精准投递
 */
@Component
public class WebSocketSessionManager {

    // userAccount → 该用户所有 sessionId（一个用户可能多端登录）
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();

    // userId → 该用户所有 sessionId
    private final Map<Long, Set<String>> userIdSessions = new ConcurrentHashMap<>();

    // sessionId → userAccount（反向映射，断开时快速查找）
    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();

    // sessionId → userId（反向映射）
    private final Map <String, Long> sessionUserIds = new ConcurrentHashMap<>();

    /**
     * 用户连接时注册
     */
    public void registerSession(String userAccount, Long userId, String sessionId) {
        //返回 Map<String, Set<String>> 当中的  Set<String>  ，然后用add将新的会话内容添加进去
        userSessions.computeIfAbsent(userAccount, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        userIdSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);

        sessionUsers.put(sessionId, userAccount);
        sessionUserIds.put(sessionId, userId);
    }

    /**
     * 用户断开时注销
     */
    public void removeSession(String sessionId) {
        String userAccount = sessionUsers.remove(sessionId);
        Long userId = sessionUserIds.remove(sessionId);

        if (userAccount != null) {
            Set<String> sessions = userSessions.get(userAccount);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    userSessions.remove(userAccount);
                }
            }
        }
        if (userId != null) {
            Set<String> sessions = userIdSessions.get(userId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    userIdSessions.remove(userId);
                }
            }
        }
    }

    /**
     * 根据 userAccount 获取指定用户的 sessionId 集合
     */
    public Set<String> getSessionIdsByAccount(String userAccount) {
        return userSessions.getOrDefault(userAccount, Collections.emptySet());
    }

    /**
     * 根据 userId 获取指定用户的 sessionId 集合（点对点消息核心方法）
     */
    public Set<String> getSessionIdsByUserId(Long userId) {
        return userIdSessions.getOrDefault(userId, Collections.emptySet());
    }

    /**
     * 判断用户是否在线（按 userId）
     */
    public boolean isOnline(Long userId) {
        Set<String> sessions = userIdSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * 获取所有在线用户
     */
    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(userSessions.keySet());
    }

    /**
     * 获取在线人数
     */
    public int getOnlineCount() {
        return userSessions.size();
    }
}
