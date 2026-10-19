package com.wang.tradingplatform.pojo.dto;

import com.wang.tradingplatform.pojo.entity.Picture;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UploadItemDTO {
    //图片（含URL、宽、高）
    private Picture[] image;
    //描述
    private String description;
    //价钱
    private BigDecimal price;
    //原价
    private BigDecimal originalPrice;
    //规格属性  电子产品，饰品这一类的标签
    private String[] specs;
    //标签  99新，急出这一类的标签
    private String[] tag;
    //用户id 后端提供
    private Long userId;
    //标签  99新，急出这一类的标签  这个是为了好存储新加的
    private Integer IntTag;
    //商品id  后端提供
    private Long goodsId;
}