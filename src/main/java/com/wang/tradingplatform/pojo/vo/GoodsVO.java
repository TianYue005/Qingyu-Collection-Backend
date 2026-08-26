package com.wang.tradingplatform.pojo.vo;

import com.wang.tradingplatform.pojo.entity.GoodsImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVO {
    // goods表字段
    private Long goodsId;
    private Long userId;
    private String goodsDesc;
    private BigDecimal price;
    private String userName;
    private BigDecimal originalPrice;
    private String tags;
    private boolean favourite;//是否已经收藏
    // 商品图片集合（一对多）
    private List<GoodsImage> imgList;
}
