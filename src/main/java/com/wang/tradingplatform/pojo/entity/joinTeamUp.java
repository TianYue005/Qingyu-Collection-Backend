package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class joinTeamUp {
    private Integer id;
    private Long userId;
    private Integer teamUpId;
}
