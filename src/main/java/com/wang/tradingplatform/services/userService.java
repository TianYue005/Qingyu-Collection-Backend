package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.User;

public interface userService {
    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果消息
     */
    String register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return
     */
    String login(LoginDTO loginDTO);

    /**
     * 根据用户账号查询用户名
     */
    String selectName(String string);

    /**
     * 根据用户账户号查用户Id
     */
    Long selectId(String account);

    /**
     * 根据用户Id查询对应的用户名与账号
     */
    User selectAccountAndName(Long userId);
}
