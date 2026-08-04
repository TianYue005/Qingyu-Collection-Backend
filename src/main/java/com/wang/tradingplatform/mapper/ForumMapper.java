package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.Circle;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.TeamUp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ForumMapper {
    //添加组团信息
    void add(TeamUp teamUp);

    //分页查询
    List<TeamUp> page(ItemQueryParam itemQueryParam);

    //添加圈子动态信息
    void addDynamic(Circle circle);

    //添加发布任务信息
    void addTask(Circle circle);

    //添加创建活动信息
    void addActivity(Circle circle);
}
