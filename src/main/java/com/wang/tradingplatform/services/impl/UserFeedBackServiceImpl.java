package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.annotation.Permission;
import com.wang.tradingplatform.mapper.FeedBackMapper;
import com.wang.tradingplatform.pojo.entity.UserFeedback;
import com.wang.tradingplatform.services.UserFeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserFeedBackServiceImpl implements UserFeedBackService {

    private final FeedBackMapper feedBackMapper;

    //用户提交反馈
    @Override
    @Permission
    public Boolean addFeedBack(UserFeedback feedback) {
        try {
            int rows = feedBackMapper.addFeedBack(feedback);
            return rows > 0;
        } catch (Exception e) {
            throw new RuntimeException("新增失败：" + e.getMessage());
        }
    }
}
