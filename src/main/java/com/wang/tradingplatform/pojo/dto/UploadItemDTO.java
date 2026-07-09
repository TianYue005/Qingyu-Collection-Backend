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
    //规格属性
    private String[] specs;
}