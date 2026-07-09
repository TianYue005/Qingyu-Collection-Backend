package com.wang.tradingplatform.controller;


import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户模块")
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private userService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        String msg = userService.register(registerDTO);
        if ("注册成功".equals(msg)) {
            return Result.success();
        }
        return Result.error(msg);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        log.info("========== 收到登录请求 ==========");
        log.info("登录用户名: {}", loginDTO.getAccount());

        try {
            String message = userService.login(loginDTO);
            if (message.equals("信息有误") || message.equals("登陆失败，请检查账号或者密码")){
                return Result.error(message);
            }
            message=message+","+ userService.selectName(loginDTO.getAccount());
            return Result.success(message);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
