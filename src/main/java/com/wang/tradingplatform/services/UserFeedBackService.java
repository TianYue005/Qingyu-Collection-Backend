package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.entity.UserFeedback;

public interface UserFeedBackService {
    //用户提交反馈
    Boolean addFeedBack(UserFeedback feedback);
}
