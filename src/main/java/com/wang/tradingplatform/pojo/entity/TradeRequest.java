package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
public class TradeRequest {
    private Long goodsId;
    private Long sessionId;
    private Integer select;
    private Long toUid;
    private Integer tradeState; //交易状态 0 未确认 1 已有请求 2已同意请求 3已拒绝 4交易已完成
}
