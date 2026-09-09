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
    private Long sold;
    private boolean favourite;//是否已经收藏
    private Integer finished;//是否已经卖掉了
    // 商品图片集合（一对多）
    private List<GoodsImage> imgList;
    /*简易图片展示相关*/
    private String imgUrl;
    private Integer imgWidth;
    private Integer imgHeight;
    //是否有评价 1为有 0为无
    private Integer hasEvaluate;
}
