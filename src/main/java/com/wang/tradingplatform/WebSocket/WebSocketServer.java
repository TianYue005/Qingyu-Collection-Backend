package com.wang.tradingplatform.WebSocket;

import io.jsonwebtoken.io.IOException;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/chat")
public class WebSocketServer {
    // 保存所有在线客户端会话
    //存在线客户端
    private static final CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<>();
    private Session session;

    /***ConcurrentLinkedQueue（推荐本地消息临时队列）**
    无锁并发队列，FIFO，高性能，用来临时缓存待发送消息*/

    // 连接建立
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        sessionSet.add(session);
        sendMsgAll("有新客户端上线，当前在线：" + sessionSet.size());
    }

    // 收到客户端消息
    @OnMessage
    public void onMessage(String msg, Session session) {
        System.out.println("收到客户端消息：" + msg);
        // 广播给所有人
        sendMsgAll("服务端收到：" + msg);
    }

    // 连接关闭
    @OnClose
    public void onClose() {
        sessionSet.remove(this.session);
        sendMsgAll("客户端下线，剩余在线：" + sessionSet.size());
    }

    // 异常
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    // 群发消息
    public void sendMsgAll(String text) {
        for (Session s : sessionSet) {
            try {
                s.getBasicRemote().sendText(text);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
