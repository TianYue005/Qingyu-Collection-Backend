package com.wang.tradingplatform.pojo.vo;

import com.wang.tradingplatform.pojo.entity.GoodsImage;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsVO {
    // goods表字段
    private Long goodsId;
    private Long userId;
    private String goodsDesc;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String tags;
    // 商品图片集合（一对多）
    private List<GoodsImage> imgList;
}
