package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pending {
    private Long goodsId;//商品id
    private String goodsDesc;//商品描述
    private BigDecimal price;//价格
    private String picture;//商品图片（只擦存储一张）
    private Long sold;//是否出售 未出售为0 出售则为买家的用户id
}
