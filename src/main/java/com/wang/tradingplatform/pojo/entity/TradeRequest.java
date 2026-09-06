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
    private Integer tradeState;
}
