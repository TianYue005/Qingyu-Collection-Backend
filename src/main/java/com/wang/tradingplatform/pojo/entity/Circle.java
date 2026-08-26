package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private String category;
    //这里的category与type进行区分
    //category是区分Dynamics，Task，Event三个
    //type是区分圈子动态的求助等与跑图任务的代取快递等
    //这两个是不一样的
}
