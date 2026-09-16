package com.wang.tradingplatform.pojo.dto;

import lombok.Data;

@Data
public class EvaluateDTO {
    private Long goodsId;//商品id（前端传递）
    private String content;//评价内容
    private Integer score;//评价分数
}
