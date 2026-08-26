package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.CommentGoods;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.vo.CommentGoodsVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;

public interface itemsServices {
    //商品上架接口
    Boolean add(UploadItemDTO uploadItemDTO);

    //分页查找
    PageResult<GoodsVO> toPage(ItemQueryParam itemQueryParam);

    //根据ID查找商品
    GoodsVO findGoodsById(Long id);

    //根据关键词查询相关商品信息
    PageResult<GoodsVO> selectByKeyword(String keyword);

    //查找用户发布的商品
    PageResult<GoodsVO> selectMyGoods();

    //商品评论
    void addItemComment(CommentGoods comment);

    //分页查询商品评论
    PageResult<CommentGoodsVO> selectItemComment(Long goodsId);

    //查看该评论之前的所有互动
    PageResult<CommentGoodsVO> selectItemCommentInteraction(Long id);
}
