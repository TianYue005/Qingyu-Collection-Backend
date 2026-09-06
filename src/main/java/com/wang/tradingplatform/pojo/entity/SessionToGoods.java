package com.wang.tradingplatform.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionToGoods {
    private Long id;
    private Long sessionId;
    private Long goodsId;
    private LocalDateTime createTime;
}
