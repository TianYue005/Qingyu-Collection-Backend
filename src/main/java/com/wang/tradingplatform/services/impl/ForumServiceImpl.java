package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.exception.BusinessException;
import com.wang.tradingplatform.exception.TeamUpTypeException;
import com.wang.tradingplatform.mapper.ForumMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentCircleVO;
import com.wang.tradingplatform.pojo.vo.CommentVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.ForumService;
import com.wang.tradingplatform.utils.ParamUtil;
import com.wang.tradingplatform.utils.RedisUtil;
import com.wang.tradingplatform.utils.UserContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class ForumServiceImpl implements ForumService {
    private final ForumMapper forumMapper;
    private final UserMapper userMapper;
    private final RedisUtil redisUtil;

    public ForumServiceImpl(ForumMapper forumMapper, UserMapper userMapper, RedisUtil redisUtil) {
        this.forumMapper = forumMapper;
        this.userMapper = userMapper;
        this.redisUtil = redisUtil;
    }

    private static final List<String> ALLOWED_TYPES = List.of("all", "study", "movie", "dinner", "carpool", "order", "game", "sport", "travel", "other");


    //添加组团信息
    @Override
    public void add(TeamUp teamUp) {
        ParamUtil.notNull(teamUp, "组团信息");
        ParamUtil.notBlank(teamUp.getTitle(), "组团标题");
        ParamUtil.notBlank(teamUp.getStartTime(), "组团开始时间");
        String type = teamUp.getType();
        if (teamUp.getPeopleNumber() == null || teamUp.getPeopleNumber() > 10 || teamUp.getPeopleNumber() < 1) {
            throw new BusinessException("允许参加的人数必须在1到10之间");
        }
        if (!ALLOWED_TYPES.contains(type)) {
            throw new TeamUpTypeException("传递的组团分类是不被允许的类型");
        }
        Long currentUserId = UserContext.getCurrentUserId();

        teamUp.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));//设置创建时间
        teamUp.setLeader(currentUserId);//设置领导者id
        teamUp.setLeaderName(userMapper.selectUserNameById(currentUserId));
        forumMapper.add(teamUp);
    }

    //分组分页查询
    @Override
    public PageResult<TeamUp> select(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
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
        ParamUtil.notNull(circle, "动态信息");
        ParamUtil.notBlank(circle.getContent(), "动态内容");
        circle.setUserId(UserContext.getCurrentUserId());
        forumMapper.addDynamic(circle);
    }

    //分页查询 圈子动态
    @Override
    public PageResult<Circle> selectDynamic(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
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
        ParamUtil.notNull(circle, "任务信息");
        ParamUtil.notBlank(circle.getTitle(), "任务标题");
        circle.setUserId(UserContext.getCurrentUserId());
        forumMapper.addTask(circle);
    }

    //分页查询 发布任务
    @Override
    public PageResult<Circle> selectTask(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
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
        ParamUtil.notNull(circle, "活动信息");
        ParamUtil.notBlank(circle.getTitle(), "活动标题");
        circle.setUserId(UserContext.getCurrentUserId());
        circle.setStatus(0);
        forumMapper.addActivity(circle);
    }

    //分页查询 热门活动
    @Override
    public PageResult<Circle> selectActivity(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
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
        ParamUtil.notBlank(keyword, "搜索关键词");
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
        ParamUtil.notBlank(keyword, "搜索关键词");
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
        ParamUtil.notBlank(keyword, "搜索关键词");
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
        ParamUtil.notBlank(keyword, "搜索关键词");
        try (Page<Circle> page = PageHelper.startPage(
                1, 10
        )) {
            List<Circle> CircleList = forumMapper.searchActivity(keyword);
            return new PageResult<>(page.getTotal(), CircleList);
        }
    }

    //我的参与
    @Override
    public PageResult<TeamUp> myJoin(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
        try (Page<TeamUp> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize()
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
        ParamUtil.positive(id, "组团id");
        return forumMapper.detailedTeamUp(id, UserContext.getCurrentUserId());
    }

    //参加组团
    @Override
    public void join(Long teamUpId) {
        ParamUtil.positive(teamUpId, "组团id");
        TeamUp t = forumMapper.selectLeft(teamUpId);
        if (t == null) {
            throw new BusinessException("组团不存在");
        }
        if (t.getPeopleNumber() - t.getParticipateNumber() <= 0) {
            throw new BusinessException("超出允许参加的人数");
        }
        try {
            forumMapper.join(teamUpId, UserContext.getCurrentUserId());
        } catch (DuplicateKeyException e) {
            throw new BusinessException("你已参加过该组团");
        }
    }

    //组团评论
    @Override
    public void addComment(Comment comment) {
        ParamUtil.notNull(comment, "评论信息");
        ParamUtil.positive(comment.getTeamupId(), "组团id");
        ParamUtil.notBlank(comment.getText(), "评论内容");
        comment.setUserId(UserContext.getCurrentUserId());
        comment.setCreateTime(LocalDateTime.now());
        forumMapper.addComment(comment);
        //热度相关
        redisUtil.zAdd(redisUtil.REDIS_HOT_SORT_KEY, comment.getTeamupId(), 100);
    }

    //分页查询组团评论
    @Override
    public PageResult<CommentVO> selectComment(Long teamUpId) {
        ParamUtil.positive(teamUpId, "组团id");
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
        ParamUtil.positive(commentId, "评论id");
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
        ParamUtil.notNull(comment, "评论信息");
        ParamUtil.positive(comment.getCircleId(), "圈子id");
        ParamUtil.notBlank(comment.getText(), "评论内容");
        comment.setUserId(UserContext.getCurrentUserId());
        comment.setCreateTime(LocalDateTime.now());
        forumMapper.addCommentCircle(comment);
    }

    //查看圈子评论
    @Override
    public PageResult<CommentCircleVO> selectCircleComment(Long circleId) {
        ParamUtil.positive(circleId, "圈子id");
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
        ParamUtil.positive(id, "评论id");
        try (Page<CommentCircleVO> page = PageHelper.startPage(
                1, 10
        )) {
            List<CommentCircleVO> CommentList = forumMapper.selectCircleCommentInteraction(id);
            return new PageResult<>(page.getTotal(), CommentList);
        }
    }

    //圈子详细信息
    @Override
    public Circle detailedCircleUpdates(Integer id, String option) {
        ParamUtil.positive(id, "圈子id");
        ParamUtil.notBlank(option, "圈子类型");
        Long currentUserId = UserContext.getCurrentUserId();
        if (option.equals("Dynamic")) {
            return forumMapper.detailedDynamicUpdates(id, currentUserId);
        } else if (option.equals("Task")) {
            return forumMapper.detailedTaskUpdates(id, currentUserId);
        } else if (option.equals("Activity")) {
            return forumMapper.detailedActivityUpdates(id, currentUserId);
        } else {
            throw new BusinessException("传递的圈子类型不正确");
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

    //查看圈子的热榜
    @Override
    public Set<Object> selectHortSortCircle() {
        //从最终热度榜单中取出圈子id集合（key与DailyFourTask中的REDIS_HOT_SORT_KEY_FINAL保持一致）
        return redisUtil.zReverseRange("like:cirle:final", 0, -1);
    }

    //根据id列表查询对应的简略信息
    @Override
    public List<Circle> selectSimpleInfoByList(List<Long> idList) {
        return forumMapper.selectSimpleInfoByList(idList);
    }

    //圈子的点赞接口
    @Override
    public void setCircleLike(Long circleId) {
        ParamUtil.positive(circleId, "圈子id");
        Integer row = forumMapper.setCircleLike(circleId, UserContext.getCurrentUserId());
        if (row == 0) {
            throw new BusinessException("不可以重复点赞");
        }
        //运行到这里说明成功实现一次点赞
        //为它在redis临时热度榜单中添加score（key与DailyFourTask中的REDIS_HOT_SORT_KEY保持一致）
        redisUtil.zAdd("like:circle", circleId, 50);
    }

    //接受跑腿任务
    @Override
    public void acceptTask(Long circleId) {
        ParamUtil.positive(circleId, "任务id");
        forumMapper.acceptTask(circleId, UserContext.getCurrentUserId());
    }

    //我参与的跑腿任务
    @Override
    public PageResult<Circle> myTakeTask(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
        try (Page<Circle> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize())) {
            List<Circle> list = forumMapper.myTakeTask(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), list);
        }
    }
}
