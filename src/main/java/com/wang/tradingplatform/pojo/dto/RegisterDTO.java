package com.wang.tradingplatform.pojo.dto;

import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterDTO {
    //用户邮箱或者手机号
    private String account;
    //密码
    private String password;





    /*//用户名
    private String username;
    //密码
    private String password;
    //手机号
    private String phone;*/
}
