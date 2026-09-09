package com.wang.tradingplatform.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomCodeUtil {

    // 包含大小写字母和数字的字符池 (共62个字符)
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 6;

    // 使用 SecureRandom 保证密码学级别的随机安全性
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成指定长度的随机字符串（默认6位）
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    /**
     * 生成指定长度的随机字符串
     * @param length 字符串长度
     */
    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }
}