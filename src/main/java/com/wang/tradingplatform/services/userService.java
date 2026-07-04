package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.RegisterDTO;

public interface userService {
    /**
     * 用户注册
     * @param registerDTO 注册请求参数
     * @return 注册结果消息
     */
    String register(RegisterDTO registerDTO);
}
