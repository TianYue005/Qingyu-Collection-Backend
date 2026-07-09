package com.wang.tradingplatform.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsImage {
    private Long id;//图片主键ID
    private Long goodsId;//关联商品ID
    private String imgUrl;//单张图片链接
    private Integer imgWidth;//图片宽度(px)
    private Integer imgHeight;//图片高度(px)
    private LocalDateTime createTime;//上传时间
}
