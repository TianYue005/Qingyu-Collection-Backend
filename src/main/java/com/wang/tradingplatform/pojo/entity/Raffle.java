package com.wang.tradingplatform.pojo.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Raffle {
    //描述文本
    private String text;
    //积分数值
    private Integer points;
    //是否已经抽过奖
    private boolean take;

    public Raffle(String text, Integer points) {
        this.points = points;
    }


    public Raffle(Integer points, boolean take) {
        this.points = points;
        this.take = take;
    }

    public Raffle(Integer points, boolean take, String text) {
        this.points = points;
        this.take = take;
        this.text = text;
    }
}
