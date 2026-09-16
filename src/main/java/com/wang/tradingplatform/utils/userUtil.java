package com.wang.tradingplatform.utils;

import com.wang.tradingplatform.exception.BusinessException;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class userUtil {
    private final UserMapper userMapper;

    //校验注册信息，不符合要求则抛出运行时异常，异常信息说明具体原因
    public void checkRegisterDTO(@NonNull RegisterDTO registerDTO) {
        ParamUtil.notBlank(registerDTO.getUsername(), "用户名");
        ParamUtil.notBlank(registerDTO.getAccount(), "账号");
        //校验账户是否重复（根据账号查用户ID，查到即已存在）
        Long userId = userMapper.findIDByAccount(registerDTO.getAccount());
        if (userId != null) {
            throw new BusinessException("该账号已被注册");
        }
        ParamUtil.notBlank(registerDTO.getPassword(), "密码");
        //校验密码长度（至少6位）
        if (registerDTO.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
    }

    public void checkLoginDTO(@NonNull LoginDTO loginDTO) {
        ParamUtil.notBlank(loginDTO.getAccount(), "账号");
        ParamUtil.notBlank(loginDTO.getPassword(), "密码");
    }
}
