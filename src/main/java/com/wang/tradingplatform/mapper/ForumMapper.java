package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentCircleVO;
import com.wang.tradingplatform.pojo.vo.CommentVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForumMapper {
    //添加组团信息
    void add(TeamUp teamUp);

    //分组分页查询
    List<TeamUp> TeamUpPage(ItemQueryParam itemQueryParam);

    //添加圈子动态信息
    void addDynamic(Circle circle);

    //添加发布任务信息
    void addTask(Circle circle);

    //添加创建活动信息
    void addActivity(Circle circle);

    //分页查询 圈子动态
    List<Circle> DynamicPage( ItemQueryParam itemQueryParam);

    //分页查询 发布任务
    List<Circle> TaskPage(ItemQueryParam itemQueryParam);

    //分页查询 热门活动
    List<Circle> ActivityPage(ItemQueryParam itemQueryParam);

    //组团模糊搜索
    List<TeamUp> searchTeamUp(String keyword);

    //圈子动态模糊搜索
    List<Circle> searchDynamic(String keyword);

    //发布任务模糊搜索
    List<Circle> searchTask(String keyword);

    //创建活动模糊搜索
    List<Circle> searchActivity(String keyword);

    //我的参与
    List<TeamUp> myJoin(Long userId);

    //我的创建
    List<TeamUp> myCreate(Long currentUserId);

    //根据组团id查询对应的详细信息
    TeamUp detailedTeamUp(Long id,Long userId);

    //组团评论
    void addComment(Comment comment);

    //查询组团评论
    List<CommentVO> selectComment(Long teamUpId);

    //查询指定评论id的全部信息
    CommentVO searchCommentById(Integer id);

    //查看该id的评论的父评论的id
    Integer searchCommentParentId(Integer id);

    //查看某条评论之前的所有互动（根据评论id向上追溯祖先评论链）
    List<CommentVO> selectCommentInteraction(Integer commentId);

    //圈子评论
    void addCommentCircle(CommentCircle comment);

    //查看圈子评论
    List<CommentCircleVO> selectCircleComment(Long circleId);

    //查看该评论之前的所有互动
    List<CommentCircleVO> selectCircleCommentInteraction(Integer commentId);

    //圈子动态详细信息
    Circle detailedDynamicUpdates(@Param("id") Integer id, @Param("currentUserId") Long currentUserId);

    //跑腿任务详细信息
    Circle detailedTaskUpdates(@Param("id") Integer id, @Param("currentUserId") Long currentUserId);

    //热门活动详细信息
    Circle detailedActivityUpdates(@Param("id") Integer id, @Param("currentUserId") Long currentUserId);

    //查看我参加的圈子
    List<Circle> myParticipateCircle(Long currentUserId);

    //组团的点赞接口
    Integer setCircleLike(Long circleId, Long currentUserId);

    //根据id列表查询对应的简略信息
    List<Circle> selectSimpleInfoByList(List<Long> idList);

    //参加组团
    void join(Long teamUpId, Long currentUserId);

    //看还有没有剩下的人数
    TeamUp selectLeft(Long teamUpId);

    //接受跑腿任务
    void acceptTask(Long circleId, Long currentUserId);

    //我参与的跑腿任务
    List<Circle> myTakeTask(Long currentUserId);

    //热门活动的点赞接口
    Integer setActivityLike(Long activityId, Long currentUserId);

    //加入热门活动
    void joinActivity(Long activityId, Long currentUserId);

    //我加入的热门活动
    List<Circle> myJoinActivity(Long currentUserId);

    //查看我发布的动态
    List<Circle> selectMyDynamic(Long currentUserId);

    //查看我发布的任务
    List<Circle> selectMyTask(Long currentUserId);

    //查看我发布的活动
    List<Circle> selectMyActivity(Long currentUserId);
}
