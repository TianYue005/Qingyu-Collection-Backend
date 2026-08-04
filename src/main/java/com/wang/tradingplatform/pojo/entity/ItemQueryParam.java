package com.wang.tradingplatform.pojo.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ItemQueryParam {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginTime;//开始时间 非必须传递
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;//结束时间 非必须传递
    Integer pageNumber = 1;//页码
    Integer pageSize = 10;//每页数量
    String sortRules;//排序规则
    String type;//论坛话题分类
}
