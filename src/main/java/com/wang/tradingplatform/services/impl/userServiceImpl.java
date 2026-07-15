package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.User;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.JwtTokenUtil;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import com.wang.tradingplatform.utils.userUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class userServiceImpl implements userService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;
    private final userUtil userUtil;
    private final SnowflakeIdUtil snowflakeIdUtil;

    @Override
    public String register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername(); // 用户名
        String account = registerDTO.getAccount(); // 手机号
        String password = registerDTO.getPassword(); // 密码
        boolean b = userUtil.checkRegisterDTO(registerDTO);
        if (!b) {
            return "输入信息有误";
        }
        // 构建用户对象
        User user = new User();
        // TODO: 密码目前明文存储
        user.setUserId(snowflakeIdUtil.nextId());
        user.setUserName(username);
        user.setAccount(account);
        user.setPassword(password);
        user.setStatus(1);                          // 1-正常
        user.setBalance(BigDecimal.ZERO);           // 初始余额0
        user.setIntegral(100);                      // 初始活跃度100
        user.setCredit(80);                         // 初始信誉分80
        user.setLevel(1);                           // 初始等级1
        user.setDeleted(0);                     // 未删除
        user.setAvatar("默认头像");
        // 插入数据库
        int rows = userMapper.insert(user);
        if (rows > 0) {
            return "注册成功";
        }
        return "注册失败";
    }

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return
     */
    @Override
    public String login(LoginDTO loginDTO) {
        //检验loginDTO的数据
        boolean b = userUtil.checkLoginDTO(loginDTO);
        if (!b) {
            return "信息有误";
        }
        //登录
        Integer login = userMapper.login(loginDTO.getAccount(), loginDTO.getPassword());
        if (login == null || login == 0) {
            return "登陆失败，请检查账号或者密码";
        }
        //生成token,并返回
        return jwtTokenUtil.generateToken(userMapper.findIDByAccount(loginDTO.getAccount()));
    }

    //根据用户账号查找对应的用户名
    @Override
    public String selectName(String account) {
        return userMapper.selectName(account);
    }

    //根据用户账号查询用户Id
    @Override
    public Long selectId(String account) {
        return userMapper.findIDByAccount(account);
    }

    //根据用户Id查询对应的用户名与账号
    @Override
    public String selectAccountAndName(Long userId) {
        return userMapper.selectAccountAndName(userId);
    }
}
