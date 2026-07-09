package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //用户ID
    private Long userId;
    //用户名
    private String userName;
    //账号
    private String account;
    //密码
    private String password;
    //状态 （1正常 2冻结）
    private Integer status;
    //创建时间
    private LocalDateTime createTime;
    //更新时间
    private LocalDateTime updateTime;
    //用户等级
    private Integer level;
    //用户活跃度
    private Integer integral;
    //用户信誉分
    private Integer credit;
    //用户余额（金钱专用类型）
    private BigDecimal balance;
    //逻辑删除 0未删 1已删
    private Integer deleted;
    //用户头像
    private String avatar;
}
