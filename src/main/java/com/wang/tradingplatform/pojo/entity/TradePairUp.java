package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradePairUp {
    private String otherVerifyCode;//验证码
    private Integer Operate;//具体操作 1
    private Long goodsId;//关联商品的商品id
}
