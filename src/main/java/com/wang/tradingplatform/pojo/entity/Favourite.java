package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Favourite {
    //收藏id
    private Long id;
    //收藏的人的id
    private Long userId;
    //对应的商品id
    private Long itemId;
}
