package com.wang.tradingplatform.utils;

import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
@RequiredArgsConstructor
public class userUtil {
    private final UserMapper userMapper;

    //检验用户表单是否为空，是否符合设定
    void validateUserForm(@NonNull RegisterDTO registerDTO) {
        // 校验用户名
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        //校验账户是否为空
        if (registerDTO.getAccount() == null || registerDTO.getAccount().trim().isEmpty()) {
            throw new RuntimeException("账户不能为空");
        }
        //校验账户是否重复
        User account = userMapper.findByAccount(registerDTO.getAccount());
        if (account != null){
            throw new RuntimeException("账户已存在");
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
