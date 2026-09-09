package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.exception.TeamUpTypeException;
import com.wang.tradingplatform.mapper.ForumMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentCircleVO;
import com.wang.tradingplatform.pojo.vo.CommentVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.ForumService;
import com.wang.tradingplatform.utils.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {
    private final ForumMapper forumMapper;
    private final UserMapper userMapper;

    public ForumServiceImpl(ForumMapper forumMapper, UserMapper userMapper) {
        this.forumMapper = forumMapper;
        this.userMapper = userMapper;
    }

    private static final List<String> ALLOWED_TYPES = List.of("all", "study", "movie", "dinner", "carpool", "order", "game", "sport", "travel", "other");


    //添加组团信息
    @Override
    public void add(TeamUp teamUp) {
        String type = teamUp.getType();
        if (!ALLOWED_TYPES.contains(type)) {
            throw new TeamUpTypeException("传递的组团分类是不被允许的类型");
        }
        Long currentUserId = UserContext.getCurrentUserId();
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formate);
        teamUp.setCreateAt(time);//设置创建时间
        teamUp.setUpdateAt(time);//设置最后更新时间
        teamUp.setLeader(currentUserId);//设置领导者id
        teamUp.setLeaderName(userMapper.selectUserNameById(currentUserId));
        forumMapper.add(teamUp);
    }

    //分组分页查询
    @Override
    public PageResult<TeamUp> select(ItemQueryParam itemQueryParam) {
        //校验是否时允许传递的排序类型，防止sql注入 todo
        //使用PageHelper进行分页处理（try-with-resources确保ThreadLocal资源被清理）
        try (Page<TeamUp> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            List<TeamUp> TeamUpList = forumMapper.TeamUpPage(itemQueryParam);
            //构造并返回分页结果对象，包含总记录数和当前页数据
            return new PageResult<TeamUp>(page.getTotal(), TeamUpList);
        }
    }

    //添加圈子动态信息
    @Override
    public void addDynamic(Circle circle) {
        circle.setUserId(UserContext.getCurrentUserId());
        forumMapper.addDynamic(circle);
    }

    //分页查询 圈子动态
    @Override
    public PageResult<Circle> selectDynamic(ItemQueryParam itemQueryParam) {
        //校验是否是允许传递的排序类型，防止sql注入 todo
        try (Page<Circle> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            List<Circle> CircleList = forumMapper.DynamicPage(itemQueryParam);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //添加发布任务信息
    @Override
    public void addTask(Circle circle) {
        circle.setUserId(UserContext.getCurrentUserId());
        forumMapper.addTask(circle);
    }

    //分页查询 发布任务
    @Override
    public PageResult<Circle> selectTask(ItemQueryParam itemQueryParam) {
        //校验是否是允许传递的排序类型，防止sql注入 todo
        try (Page<Circle> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            List<Circle> CircleList = forumMapper.TaskPage(itemQueryParam);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //添加创建活动信息
    @Override
    public void addActivity(Circle circle) {
        circle.setUserId(UserContext.getCurrentUserId());
        circle.setStatus(0);
        forumMapper.addActivity(circle);
    }

    //分页查询 热门活动
    @Override
    public PageResult<Circle> selectActivity(ItemQueryParam itemQueryParam) {
        try (Page<Circle> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            List<Circle> CircleList = forumMapper.ActivityPage(itemQueryParam);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //组团模糊搜索
    @Override
    public PageResult<TeamUp> searchTeamUp(String keyword) {
        try (Page<TeamUp> page = PageHelper.startPage(
                1, 10
        )) {
            List<TeamUp> TeamUpList = forumMapper.searchTeamUp(keyword);
            return new PageResult<>(page.getTotal(), TeamUpList);
        }
    }

    //圈子动态模糊搜索
    @Override
    public PageResult<Circle> searchDynamic(String keyword) {
        try (Page<Circle> page = PageHelper.startPage(
                1, 10
        )) {
            List<Circle> CircleList = forumMapper.searchDynamic(keyword);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //发布任务模糊搜索
    @Override
    public PageResult<Circle> searchTask(String keyword) {
        try (Page<Circle> page = PageHelper.startPage(
                1, 10
        )) {
            List<Circle> CircleList = forumMapper.searchTask(keyword);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //创建活动模糊搜索
    @Override
    public PageResult<Circle> searchActivity(String keyword) {
        try (Page<Circle> page = PageHelper.startPage(
                1, 10
        )) {
            List<Circle> CircleList = forumMapper.searchActivity(keyword);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //我的参与
    @Override
    public PageResult<TeamUp> myJoin() {
        try (Page<TeamUp> page = PageHelper.startPage(
                1, 10
        )) {
            List<TeamUp> TeamUpList = forumMapper.myJoin(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), TeamUpList);
        }
    }

    //我的创建
    @Override
    public PageResult<TeamUp> myCreate() {
        try (Page<TeamUp> page = PageHelper.startPage(
                1, 10
        )) {
            List<TeamUp> TeamUpList = forumMapper.myCreate(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), TeamUpList);
        }
    }

    //根据组团id查询对应的详细信息
    @Override
    public TeamUp detailedTeamUp(Long id) {
        return forumMapper.detailedTeamUp(id);
    }

    //组团评论
    @Override
    public void addComment(Comment comment) {
        comment.setUserId(UserContext.getCurrentUserId());
        comment.setCreateTime(LocalDateTime.now());
        forumMapper.addComment(comment);
    }

    //分页查询组团评论
    @Override
    public PageResult<CommentVO> selectComment(Long teamUpId) {
        try (Page<CommentVO> page = PageHelper.startPage(
                1, 10
        )) {
            List<CommentVO> CommentList = forumMapper.selectComment(teamUpId);
            return new PageResult<>(page.getTotal(), CommentList);
        }
    }

    /*//查询该评论在回复的那个评论（废弃）
    @Override
    public CommentVO searchComment(Integer id) {
        //查看该id的评论的父评论的id
        Integer parentId = forumMapper.searchCommentParentId(id);
        return forumMapper.searchCommentById(parentId);
    }*/

    //查看该评论之前的所有互动
    @Override
    public PageResult<CommentVO> selectCommentInteraction(Integer commentId) {
        try (Page<CommentVO> page = PageHelper.startPage(
                1, 10
        )) {
            List<CommentVO> CommentList = forumMapper.selectCommentInteraction(commentId);
            return new PageResult<>(page.getTotal(), CommentList);
        }
    }

    //圈子评论
    @Override
    public void addCircleComment(CommentCircle comment) {
        comment.setUserId(UserContext.getCurrentUserId());
        comment.setCreateTime(LocalDateTime.now());
        forumMapper.addCommentCircle(comment);
    }

    //查看圈子评论
    @Override
    public PageResult<CommentCircleVO> selectCircleComment(Long circleId) {
        try (Page<CommentCircleVO> page = PageHelper.startPage(
                1, 10
        )) {
            List<CommentCircleVO> CommentList = forumMapper.selectCircleComment(circleId);
            return new PageResult<>(page.getTotal(), CommentList);
        }
    }

    //查看该评论之前的所有互动
    @Override
    public PageResult<CommentCircleVO> selectCircleCommentInteraction(Integer id) {
        try (Page<CommentCircleVO> page = PageHelper.startPage(
                1, 10
        )) {
            List<CommentCircleVO> CommentList = forumMapper.selectCircleCommentInteraction(id);
            return new PageResult<>(page.getTotal(), CommentList);
        }
    }

    //圈子动态详细信息
    @Override
    public Circle detailedCircleUpdates(Integer id, String option) {
        if (option.equals("Dynamic")) {
            return forumMapper.detailedDynamicUpdates(id);
        } else if (option.equals("Task")) {
            return forumMapper.detailedTaskUpdates(id);
        } else if (option.equals("Activity")) {
            return forumMapper.detailedActivityUpdates(id);
        } else {
            throw new RuntimeException();
        }
    }

    //查看我参加的圈子
    @Override
    public PageResult<Circle> myParticipateCircle() {
        try (Page<Circle> page = PageHelper.startPage(
                1, 10
        )) {
            List<Circle> CircleList = forumMapper.myParticipateCircle(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }
}
