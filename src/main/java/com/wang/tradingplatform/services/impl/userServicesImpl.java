package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.User;
import com.wang.tradingplatform.services.userServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class userServicesImpl implements userServices {

    @Autowired
    private UserMapper userMapper;

    // 密码加密
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //TODO 用户注册
    @Override
    public String register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername(); // 用户名
        String phone = registerDTO.getPhone(); // 手机号
        String password = registerDTO.getPassword(); // 密码

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "手机号不能为空";
        }

        // 检查用户名是否已存在
        User existUser = userMapper.findByUserName(username);
        if (existUser != null) {
            return "用户名已存在";
        }

        // 检查手机号是否已注册
        User existPhone = userMapper.findByPhone(phone);
        if (existPhone != null) {
            return "手机号已被注册";
        }

        // 构建用户对象
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(password));  // 密码加密
        user.setPhone(phone);
        user.setStatus(1);                          // 1-正常
        user.setBalance(BigDecimal.ZERO);           // 初始余额0
        user.setIntegral(100);                      // 初始活跃度100
        user.setCredit(100);                        // 初始信誉分100
        user.setLevel(1);                           // 初始等级1
        user.setDeleted(false);                     // 未删除
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 插入数据库
        int rows = userMapper.insert(user);
        if (rows > 0) {
            return "注册成功";
        }
        return "注册失败";
    }
}
