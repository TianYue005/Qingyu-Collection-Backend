package com.wang.tradingplatform.config;

import com.wang.tradingplatform.Interceptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/**","/upload/**") // 拦截所有请求
                .excludePathPatterns("/api/user/login", "/api/user/register", "/static/**", "/error"); // 放行接口
    }

    // CORS  todo 上线换成nginx
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 对哪些路径生效
                .allowedOrigins( "http://localhost:5173")  // 允许哪个前端访问
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允许的请求方式
                .allowedHeaders("*")       // 允许携带的请求头
                .allowCredentials(true);   // 允许携带 cookie
    }
}