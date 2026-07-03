package com.wang.tradingplatform.utils;

import com.wang.tradingplatform.pojo.dto.RegisterDTO;

public class userUtil {
    //检验用户表单是否为空，是否符合设定
    public static void validateUserForm(RegisterDTO registerDTO) {
        // 校验用户名
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        // 校验密码
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        // 校验密码长度（至少6位）
        if (registerDTO.getPassword().length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }
    }
}
