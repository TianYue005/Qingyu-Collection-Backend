package com.wang.tradingplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamUp {
    private Integer id;
    private String type;//类型
    private String title;//标题
    private Long leader;//领导者
    private String leaderName;//领导者用户名
    private String startTime;//组团活动开始时间
    private String createAt;//创建时间
    private String updateAt;//最后更新时间
    private Integer peopleNumber;//允许的最多参与人数
    private Integer participateNumber;//已经参加的人数
    private Integer joined;//是否已经参与
}
