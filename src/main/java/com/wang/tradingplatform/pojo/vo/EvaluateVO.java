package com.wang.tradingplatform.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价视图对象
 * 用于展示交易双方互评信息（自己对别人的评价 / 别人对自己的评价）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateVO {
    private Long id;//评价主键ID
    private Long evaluatorId;//评价人ID
    private String evaluatorName;//评价人用户名
    private String evaluatorAvatar;//评价人头像
    private Long evaluatedId;//被评价人ID
    private String evaluatedName;//被评价人用户名
    private String evaluatedAvatar;//被评价人头像
    private String content;//评价内容
    private Integer score;//评价分数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    private Long goodsId;//商品id
    private String goodsDesc;//商品描述
    private String goodsImgUrl;//商品图片（第一张）
}
