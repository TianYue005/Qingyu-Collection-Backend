package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentCircleVO;
import com.wang.tradingplatform.pojo.vo.CommentVO;
import com.wang.tradingplatform.pojo.vo.PageResult;

public interface ForumService {
    //添加组团信息
    void add(TeamUp teamUp);

    //分组分页查询
    PageResult<TeamUp> select(ItemQueryParam itemQueryParam);

    //添加圈子动态信息
    void addDynamic(Circle circle);

    //添加发布任务信息
    void addTask(Circle circle);

    //添加创建活动信息
    void addActivity(Circle circle);

    //分页查询 圈子动态
    PageResult<Circle> selectDynamic(ItemQueryParam itemQueryParam);

    //分页查询 发布任务
    PageResult<Circle> selectTask(ItemQueryParam itemQueryParam);

    //分页查询 创建活动
    PageResult<Circle> selectActivity(ItemQueryParam itemQueryParam);

    //组团模糊搜索
    PageResult<TeamUp> searchTeamUp(String keyword);

    //圈子动态模糊搜索
    PageResult<Circle> searchDynamic(String keyword);

    //发布任务模糊搜索
    PageResult<Circle> searchTask(String keyword);

    //创建活动模糊搜索
    PageResult<Circle> searchActivity(String keyword);

    //我的参与
    PageResult<TeamUp> myJoin();

    //我的创建
    PageResult<TeamUp> myCreate();

    //根据组团id查询对应的详细信息
    TeamUp detailedTeamUp(Long id);

    //组团评论
    void addComment(Comment comment);

    //分页查询组团评论
    PageResult<CommentVO> selectComment(Long teamUpId);

    /*//查询该评论在回复的那个评论（废弃）
    CommentVO searchComment(Integer id);*/

    //查看该评论之前的所有互动
    PageResult<CommentVO> selectCommentInteraction(Integer commentId);

    //圈子评论
    void addCircleComment(CommentCircle comment);

    //查看圈子评论
    PageResult<CommentCircleVO> selectCircleComment(Long circleId);

    //查看该评论之前的所有互动
    PageResult<CommentCircleVO> selectCircleCommentInteraction(Integer id);

    //圈子动态详细信息
    Circle detailedCircleUpdates(Integer id,String option);

    //查看我参加的圈子
    PageResult<Circle> myParticipateCircle();
}
