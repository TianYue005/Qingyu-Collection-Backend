package com.wang.tradingplatform.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户反馈信息表
 * 对应表：user_feedback
 */
@Data
public class UserFeedback {

    /**
     * 反馈ID(主键)
     */
    private Long feedbackId;

    /**
     * 上传反馈的用户账号
     */
    private String uploadAccount;

    /**
     * 建议类型：1=提功能/体验建议，2=反馈故障
     */
    private Integer suggestType;

    /**
     * 建议/问题详细内容
     */
    private String suggestContent;

    /**
     * 出现问题的界面
     */
    private String problemPage;

    /**
     * 截图URL，多图逗号分隔
     */
    private String screenshot;

    /**
     * 反馈提交时间
     */
    private LocalDateTime createTime;

    /**
     * 处理状态：0待处理，1处理中，2已完结，3驳回
     */
    private Integer handleStatus;

    /**
     * 处理人员账号
     */
    private String handlerAccount;

    /**
     * 后台处理备注
     */
    private String handleNote;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
