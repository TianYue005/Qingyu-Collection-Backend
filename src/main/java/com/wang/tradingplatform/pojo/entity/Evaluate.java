package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evaluate {
    private Long id;//主键ID
    private Long evaluatorId;//评价人ID
    private Long evaluatedId;//被评价人ID
    private String content;//评价内容
    private Integer score;//评价分数
    private LocalDateTime createTime;//创建时间
    private Long goodsId;//商品id
}
