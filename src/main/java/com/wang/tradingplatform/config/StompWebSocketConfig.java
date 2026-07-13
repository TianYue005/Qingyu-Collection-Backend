package com.wang.tradingplatform.config;

import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.utils.JwtTokenUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker // 开启STOMP消息代理
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;

    public StompWebSocketConfig(JwtTokenUtil jwtTokenUtil, UserMapper userMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userMapper = userMapper;
    }

    // 注册连接端点，前端访问地址 /stomp/ws?token=xxx
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/ws")
                .setAllowedOriginPatterns("*") // 允许跨域
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
                        // 从 URL 参数中获取 token
                        if (request instanceof ServletServerHttpRequest servletRequest) {
                            String token = servletRequest.getServletRequest().getParameter("Authorization");
                            if (token != null && !token.isEmpty()) {
                                try {
                                    String userAccount = jwtTokenUtil.getUserAccountFromToken(token);
                                    Long userId = userMapper.findIDByAccount(userAccount);
                                    // 存入 WebSocket session 属性，后续在 Controller 中读取
                                    attributes.put("userAccount", userAccount);
                                    attributes.put("userId", userId);
                                    return true;
                                } catch (Exception e) {
                                    // token 无效，拒绝握手
                                    return false;
                                }
                            }
                        }
                        return false;
                    }

                    @Override
                    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                               @NonNull WebSocketHandler wsHandler, Exception exception) {
                    }
                })
                .withSockJS(); // 兼容低版本浏览器，降级轮询
    }

    // 配置消息代理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 全局客户端接收消息的前缀  在设置RabbitMQ相关的信息 为了使用RabbitMQ
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        // 客户端发送消息到服务端的接口前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点消息的用户前缀，convertAndSendToUser 会路由到 /user/{username}/...
        registry.setUserDestinationPrefix("/user");
    }
}
