package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Mapper
public interface ItemsMapper {
    //上架商品
    @Transactional
    int insert(Goods items);

    //根据ID查找商品
    Goods selectById(Long id);

    //查找所有商品
    List<Goods> selectAll();

    //分页查询
    List<Goods> page(ItemQueryParam itemQueryParam);
}
