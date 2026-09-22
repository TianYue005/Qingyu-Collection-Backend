package com.wang.tradingplatform.pojo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class riskAssessment {
    Integer GoodReview;//好评数量
    Integer BadReview;//差评
    Integer inCommentCnt;//收到的评论数量
    Integer sellCnt;//售出数目
}
