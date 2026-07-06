package com.wang.tradingplatform.utils;

import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class userUtil {
    private final UserMapper userMapper;

    //检验用户表单是否为空，是否符合设定
    public boolean checkRegisterDTO(@NonNull RegisterDTO registerDTO) {
        // 校验用户名
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            return false;
        }
        //校验账户是否为空
        if (registerDTO.getAccount() == null || registerDTO.getAccount().trim().isEmpty()) {
            return false;
        }
        //校验账户是否重复
        User account = userMapper.findByAccount(registerDTO.getAccount());
        if (account != null) {
            return false;
        }
        // 校验密码
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            return false;
        }
        // 校验密码长度（至少6位）
        return registerDTO.getPassword().length() >= 6;
    }

    public boolean checkLoginDTO(@NonNull LoginDTO loginDTO){
        //判断账号
        if (loginDTO.getAccount()==null||loginDTO.getAccount().trim().isEmpty()){
            return false;
        }
        //判断密码
        return loginDTO.getPassword() != null && !loginDTO.getPassword().trim().isEmpty();
    }
}
