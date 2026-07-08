package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.tradingplatform.mapper.ItemsMapper;
import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.itemsServices;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class itemsServicesImpl implements itemsServices {

    private final ItemsMapper itemsMapper;
    private final SnowflakeIdUtil snowflakeIdUtil;

    @Override
    @Transactional
    public Boolean add(UploadItemDTO uploadItemDTO) {
        Goods goods = new Goods();
        goods.setGoodsId(snowflakeIdUtil.nextId());
        //商品描述
        goods.setGoodsDesc(uploadItemDTO.getDescription());
        //售卖价格
        goods.setPrice(uploadItemDTO.getPrice());
        //原价
        goods.setOriginalPrice(uploadItemDTO.getOriginalPrice());
        //商品标签
        goods.setTags(uploadItemDTO.getSpecs() != null ? String.join(",", uploadItemDTO.getSpecs()) : "");
        //逻辑删除 0未删 1已删
        goods.setIsDeleted(0);

        int rows = itemsMapper.insert(goods);
        return rows > 0;
    }

    @Override
    public Goods findById(Long id) {
        return itemsMapper.selectById(id);
    }

    //分页查找
    @Override
    public PageResult<Goods> toPage(ItemQueryParam itemQueryParam) {
        //使用pagehelper进行分页处理
        PageHelper.startPage(itemQueryParam.getPageNumber(), itemQueryParam.getPageSize(),itemQueryParam.getSortRules());
        //调用mapper接口执行查询
        List<Goods> students = itemsMapper.page(itemQueryParam);
        //将查询结果转换为page类型，以便获取分页信息
        Page<Goods> p = (Page<Goods>) students;
        //构造并返回分页结果对象，包含总记录数和当前页数据
        return new PageResult<Goods>(p.getTotal(), p.getResult());
    }
}