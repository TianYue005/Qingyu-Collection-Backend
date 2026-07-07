package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Items {
    private Long id;
    private String imageStr;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String specsStr;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
