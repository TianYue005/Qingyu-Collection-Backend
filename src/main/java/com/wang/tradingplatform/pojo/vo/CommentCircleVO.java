package com.wang.tradingplatform.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentCircleVO {
    private Integer id;//评论自己的id
    private Integer circleId;//绑定的对应的组团讨论的id（前端传递）
    private Integer parentId;//等于零说明他是父，大于0说明有父（前端传递）
    private String text;//评论内容（前端传递）
    private String picture;//评论内容包含的图片（有多个则用","分割）（前端传递）
    private Integer son;//是否有回复(0是没有，其他就是有几个子评论)
    private LocalDateTime createTime;//创建时间
    private Integer level;//评论等级 (这里是最大的是最大父评论，小的是子评论)

    private Long userId;//发送评论用户的id
    private String userAvatar;//发送评论用户的头像
    private String userName;//发送评论用户的用户名

    private Long replyUserId;//被回复的用户的id（前端传递）
    private String replyUserAvatar;//被回复的用户的头像
    private String replyUserName;//被回复的用户的用户名

}
