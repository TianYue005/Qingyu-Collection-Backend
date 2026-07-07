package com.wang.tradingplatform.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UploadItemDTO {
    //图片
    private String[] image;
    //描述
    private String description;
    //价钱
    private BigDecimal price;
    //原价
    private BigDecimal originalPrice;
    //规格属性
    private String[] specs;
}