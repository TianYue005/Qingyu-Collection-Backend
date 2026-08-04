package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Circle {
    //圈子模块的复合JavaBean模板
    private Integer id;
    private Long userId;
    private String type;
    private String content;
    private Integer bounty;
    private String requestContent;
    private String note;
    private String title;
    private String picture;
    private String startTime;
    private String endTime;
    private Integer status;//0 未开始，1 已开始 2 已结束
    private Integer participant;
}
