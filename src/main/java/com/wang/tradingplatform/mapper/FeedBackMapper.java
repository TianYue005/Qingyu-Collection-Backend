package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.UserFeedback;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedBackMapper {
    //添加反馈
    int addFeedBack(UserFeedback feedback);
}
