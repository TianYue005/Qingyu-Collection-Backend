package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;

public interface userService {
    /**
     * 用户注册
     * @param registerDTO 注册请求参数
     * @return 注册结果消息
     */
    String register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    String login(LoginDTO loginDTO);
    /**
     * 查询用户名
     */
    String selectName(String string);
}
