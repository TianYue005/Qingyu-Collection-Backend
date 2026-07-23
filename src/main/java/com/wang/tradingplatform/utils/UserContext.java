package com.wang.tradingplatform.utils;

public class UserContext {
    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<String> JWT_USER = new ThreadLocal<>();

    // 存入当前用户Id
    public static void setCurrentUserId(Long Id) {
        CURRENT_USER.set(Id);
    }

    //取出当前用户Id
    public static Long getCurrentUserId() {
        return CURRENT_USER.get();
    }

    // 存入当前用户jwt
    public static void setJwtUser(String jwt) {
        JWT_USER.set(jwt);
    }

    // 取出当前用户jwt
    public static String getJwtUser() {
        return JWT_USER.get();
    }

    // 请求结束后必须清理，防止内存泄漏
    public static void clear() {
        CURRENT_USER.remove();
        JWT_USER.remove();
    }
}