package com.wang.tradingplatform.Interceptor;

import com.wang.tradingplatform.utils.JwtTokenUtil;
import com.wang.tradingplatform.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class Interceptor implements HandlerInterceptor {
    @Value("${jwt.tokenTitle}")
    private String tokenTitle;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头中获取 token
        String token = request.getHeader(tokenTitle);
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        // 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 从 token 中获取用户账号
            String userAccount = jwtTokenUtil.getUserAccountFromToken(token);
            // 将用户信息存入 ThreadLocal
            UserContext.setCurrentUser(userAccount);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期，请重新登录\"}");
            return false;
        }
    }

    //Controller 执行完毕，视图渲染之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    //类似 finally
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //释放UserContext
        UserContext.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
