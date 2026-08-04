package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.mapper.ForumMapper;
import com.wang.tradingplatform.pojo.entity.Circle;
import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.TeamUp;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
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

    public ForumServiceImpl(ForumMapper forumMapper) {
        this.forumMapper = forumMapper;
    }

    //添加组团信息
    @Override
    public void add(TeamUp teamUp) {
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formate);
        teamUp.setCreatedAt(time);//设置创建时间
        teamUp.setUploadAt(time);//设置最后更新时间
        forumMapper.add(teamUp);
    }

    //分页查询
    @Override
    public PageResult<TeamUp> select(ItemQueryParam itemQueryParam) {
        //使用PageHelper进行分页处理（try-with-resources确保ThreadLocal资源被清理）
        try (Page<TeamUp> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            List<TeamUp> TeamUpList = forumMapper.page(itemQueryParam);
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

    //添加发布任务信息
    @Override
    public void addTask(Circle circle) {
        circle.setUserId(UserContext.getCurrentUserId());
        forumMapper.addTask(circle);
    }

    //添加创建活动信息
    @Override
    public void addActivity(Circle circle) {
        circle.setUserId(UserContext.getCurrentUserId());
        circle.setStatus(0);
        forumMapper.addActivity(circle);
    }
}
