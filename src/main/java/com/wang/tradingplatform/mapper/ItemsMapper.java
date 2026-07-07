package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.Items;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemsMapper {
    //上架商品
    int insert(Items items);

    //根据ID查找商品
    Items selectById(Long id);

    //查找所有商品
    List<Items> selectAll();
}
