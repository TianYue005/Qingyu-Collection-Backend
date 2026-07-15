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
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker // 开启STOMP消息代理
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;

    // 构造函数注入
    public StompWebSocketConfig(JwtTokenUtil jwtTokenUtil, UserMapper userMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userMapper = userMapper;
    }

    // 1. 定义一个简单的 Principal 实现类，用于存放用户 ID
    public static class StompUserPrincipal implements Principal {
        private final String name;

        public StompUserPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name; // 这个返回值必须与 convertAndSendToUser 的第一个参数一致
        }

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
                            String token = servletRequest.getServletRequest().getParameter("token");
                            if (token != null && !token.isEmpty()) {
                                try {
                                    String userId = new JwtTokenUtil().getUserIdFromToken(token);
                                    // 存入 WebSocket session 属性，后续在 Controller 中可以通过 @SessionAttribute 获取
                                    attributes.put("userId", userId);
                                    return true;// 允许握手
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
                // 2. 配置自定义的 HandshakeHandler，用来绑定 Principal
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(@NonNull ServerHttpRequest request,
                                                      @NonNull WebSocketHandler wsHandler,
                                                      @NonNull Map<String, Object> attributes) {
                        // 从刚才在 Interceptor 中存入的 attributes 里取出 userId
                        Long userId = (Long) attributes.get("userId");
                        if (userId != null) {
                            // 将 userId 作为 Principal 的唯一标识返回
                            return new StompUserPrincipal(String.valueOf(userId));
                        }
                        return null;
                    }
                })
                .withSockJS(); // 兼容低版本浏览器，降级轮询
    }

    // 配置消息代理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 全局客户端接收消息的前缀
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("192.168.72.128")// RabbitMQ的IP
                .setRelayPort(61613) // STOMP 插件默认端口
                .setClientLogin("admin")//RabbitMQ的账号
                .setClientPasscode("password");//RabbitMQ的密码

        // 客户端发送消息到服务端的接口前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点消息的用户前缀，convertAndSendToUser 会路由到 /user/{username}/...
        registry.setUserDestinationPrefix("/user");
    }
}