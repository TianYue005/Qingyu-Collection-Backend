package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private Long goodsId;//商品id
    private Long userId;//所属用户ID，关联用户表主键
    private String goodsDesc;//商品描述
    private BigDecimal price;//售卖价格
    private BigDecimal originalPrice;//原价
    private String tags;//商品标签
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//最后更新时间
    private Integer isDeleted;//逻辑删除 0未删 1已删
    private List<GoodsImage> images;//关联的图片列表
    private Long sold;//0说明没有卖出，非0则卖给了sold所指向的用户id
}
