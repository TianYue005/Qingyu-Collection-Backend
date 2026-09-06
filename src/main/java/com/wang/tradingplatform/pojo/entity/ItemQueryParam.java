package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemQueryParam {
    /*基础部分*/
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginTime;//开始时间 非必须传递
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;//结束时间 非必须传递
    Integer pageNumber = 1;//页码
    Integer pageSize = 10;//每页数量

    /*附加部分*/
    //还没想好
    String sortRules;//排序规则 暂时还没有想好相关用法 todo 暂未启用
    /*圈子模块*/
    //活动部分的状态
    Integer status;//0 未开始，1 已开始 2 已结束
    /*组团模块*/
    //组团的分类 自习，游戏，电影......
    String type;
    /*聊天部分*/
    //sessionId
    private Long sessionId;
    /*评论相关*/
    //评论特质 0全部（是指该用户对其他人的评论） 1好评 2差评 3来自卖家（只有来自卖家部分能看到卖家的评论）
    private Integer trait;
}
