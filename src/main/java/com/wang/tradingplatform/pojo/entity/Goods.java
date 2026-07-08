package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private Long goodsId;//商品id
    private String goodsDesc;//商品描述
    private BigDecimal price;//售卖价格
    private BigDecimal originalPrice;//原价
    private String tags;//商品标签
    private Integer isDeleted;//逻辑删除 0未删 1已删
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//最后更新时间
}
