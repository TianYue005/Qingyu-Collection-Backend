package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {
    //图片链接
    private String url;
    //图片长度
    private Integer width;
    //图片宽度
    private Integer height;
}
