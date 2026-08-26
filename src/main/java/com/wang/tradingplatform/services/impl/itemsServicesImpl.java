package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.annotation.Permission;
import com.wang.tradingplatform.mapper.ItemsMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentGoodsVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.itemsServices;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import com.wang.tradingplatform.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class itemsServicesImpl implements itemsServices {
    private final UserMapper userMapper;
    private final ItemsMapper itemsMapper;
    private final SnowflakeIdUtil snowflakeIdUtil;

    //添加商品
    @Override
    @Permission
    @Transactional
    public Boolean add(UploadItemDTO uploadItemDTO) {
        Goods goods = new Goods();
        Long goodsId = snowflakeIdUtil.nextId();
        goods.setGoodsId(goodsId);
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
        goods.setUserId(UserContext.getCurrentUserId());
        int rows = itemsMapper.insert(goods);
        //插入商品图片（含宽高）
        Picture[] images = uploadItemDTO.getImage();
        if (images != null) {
            for (Picture pic : images) {
                GoodsImage goodsImage = new GoodsImage();
                goodsImage.setGoodsId(goodsId);
                goodsImage.setImgUrl(pic.getUrl());
                goodsImage.setImgWidth(pic.getWidth());
                goodsImage.setImgHeight(pic.getHeight());
                itemsMapper.insertImage(goodsImage);
            }
        }
        return rows > 0;
    }


    //分页查找
    @Override
    @Permission
    public PageResult<GoodsVO> toPage(ItemQueryParam itemQueryParam) {
        //使用PageHelper进行分页处理（try-with-resources确保ThreadLocal资源被清理）
        try (Page<Goods> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            //调用mapper接口执行查询 查到的数据是没有图片的
            List<GoodsVO> goodsList = itemsMapper.page(itemQueryParam, UserContext.getCurrentUserId());
            //构造并返回分页结果对象，包含总记录数和当前页数据
            return new PageResult<GoodsVO>(page.getTotal(), goodsList);
        }
    }

    //根据商品ID查询商品信息
    @Override
    @Permission
    public GoodsVO findGoodsById(Long id) {
        GoodsVO goodsById = itemsMapper.findGoodsById(id);
        //查看某商品是否被收藏
        Integer is = itemsMapper.isFavourite(id, UserContext.getCurrentUserId());
        if (is != null && id != 0 ) {
            goodsById.setFavourite(true);
        }
        return goodsById;
    }

    //根据关键词查询相关商品信息
    @Override
    @Permission
    public PageResult<GoodsVO> selectByKeyword(String keyword) {
        List<GoodsVO> goodsList = itemsMapper.selectByKeyword(keyword, UserContext.getCurrentUserId());
        return new PageResult<>((long) goodsList.size(), goodsList);
    }

    //查找用户发布的商品
    @Override
    @Permission
    public PageResult<GoodsVO> selectMyGoods() {
        List<GoodsVO> goodsList = itemsMapper.selectByUserId(UserContext.getCurrentUserId());
        return new PageResult<>((long) goodsList.size(), goodsList);
    }

    //商品评论
    @Override
    public void addItemComment(CommentGoods comment) {
        comment.setUserId(UserContext.getCurrentUserId());
        comment.setCreateTime(LocalDateTime.now());
        itemsMapper.addCommentItem(comment);
    }

    //分页查询商品评论
    @Override
    public PageResult<CommentGoodsVO> selectItemComment(Long goodsId) {
        try (Page<CommentGoodsVO> page = PageHelper.startPage(1, 10)) {
            List<CommentGoodsVO> commentList = itemsMapper.selectItemComment(goodsId);
            return new PageResult<>(page.getTotal(), commentList);
        }
    }

    //查看该评论之前的所有互动
    @Override
    public PageResult<CommentGoodsVO> selectItemCommentInteraction(Long id) {
        try (Page<CommentGoodsVO> page = PageHelper.startPage(1, 10)) {
            List<CommentGoodsVO> commentList = itemsMapper.selectItemCommentInteraction(id);
            return new PageResult<>(page.getTotal(), commentList);
        }
    }
}