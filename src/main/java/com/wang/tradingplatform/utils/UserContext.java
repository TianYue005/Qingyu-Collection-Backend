package com.wang.tradingplatform.utils;


import java.util.Objects;

public class UserContext {
    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    // 存入当前用户账号
    public static void setCurrentUser(String account) {
        CURRENT_USER.set(account);
    }

    // 请求结束后必须清理，防止内存泄漏
    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * 便捷方法：直接获取当前用户账号（唯一标识）
     *
     * @return 当前用户账号，未登录时返回 null
     */
    public static String getCurrentAccount() {
        String account = CURRENT_USER.get();
        if (account == null || account.isEmpty()) {
            throw new RuntimeException("服务器错误：未正常拿到用户账号信息");
        }
        return account;
    }
}
