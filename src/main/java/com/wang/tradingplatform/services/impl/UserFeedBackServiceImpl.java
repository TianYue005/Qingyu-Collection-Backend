package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.exception.BusinessException;
import com.wang.tradingplatform.mapper.FeedBackMapper;
import com.wang.tradingplatform.pojo.entity.UserFeedback;
import com.wang.tradingplatform.services.UserFeedBackService;
import com.wang.tradingplatform.utils.ParamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserFeedBackServiceImpl implements UserFeedBackService {

    private final FeedBackMapper feedBackMapper;

    //用户提交反馈
    @Override
    public Boolean addFeedBack(UserFeedback feedback) {
        ParamUtil.notNull(feedback, "反馈信息");
        if (feedback.getSuggestType() == null || (feedback.getSuggestType() != 1 && feedback.getSuggestType() != 2)) {
            throw new BusinessException("建议类型不合法（1=提建议，2=反馈故障）");
        }
        ParamUtil.notBlank(feedback.getSuggestContent(), "反馈内容");
        try {
            int rows = feedBackMapper.addFeedBack(feedback);
            return rows > 0;
        } catch (Exception e) {
            throw new BusinessException("新增失败：" + e.getMessage());
        }
    }
}
