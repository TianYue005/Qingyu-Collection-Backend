package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAssociationVO {
    private String goodsDesc;
    private Long goodsId;
    private BigDecimal price;
    private String imgUrl;
    private Integer imgWidth;
    private Integer imgHeight;
    //交易状态 0 未确认 1 已有请求 2已同意请求 3已拒绝
    private Integer tradeState;
    //被发送方的用户id
    private Long fromUid;
    //发送方用户id
    private Long toUid;
}
