package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.exception.BusinessException;
import com.wang.tradingplatform.mapper.ItemsMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.EvaluateDTO;
import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentGoodsVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.itemsServices;
import com.wang.tradingplatform.utils.ParamUtil;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import com.wang.tradingplatform.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class itemsServicesImpl implements itemsServices {
    private final UserMapper userMapper;
    private final ItemsMapper itemsMapper;
    private final SnowflakeIdUtil snowflakeIdUtil;

    //添加商品
    @Override
    @Transactional
    public Boolean add(UploadItemDTO uploadItemDTO) {
        ParamUtil.notNull(uploadItemDTO, "商品信息");
        ParamUtil.notBlank(uploadItemDTO.getDescription(), "商品描述");
        if (uploadItemDTO.getPrice() == null || uploadItemDTO.getPrice().signum() < 0) {
            throw new BusinessException("售卖价格不能为空且不能为负数");
        }
        if (uploadItemDTO.getOriginalPrice() != null && uploadItemDTO.getOriginalPrice().signum() < 0) {
            throw new BusinessException("原价不能为负数");
        }
        // 数据库价格字段为 DECIMAL(10,2)，最大 99999999.99，超出会插入失败
        BigDecimal maxPrice = new BigDecimal("99999999.99");
        if (uploadItemDTO.getPrice().compareTo(maxPrice) > 0) {
            throw new BusinessException("售卖价格不能超过 99999999.99 元");
        }
        if (uploadItemDTO.getOriginalPrice() != null && uploadItemDTO.getOriginalPrice().compareTo(maxPrice) > 0) {
            throw new BusinessException("原价不能超过 99999999.99 元");
        }
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
    public PageResult<GoodsVO> toPage(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
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
    public GoodsVO findGoodsById(Long id) {
        ParamUtil.positive(id, "商品id");
        GoodsVO goodsById = itemsMapper.findGoodsById(id);
        //查看某商品是否被收藏
        Integer is = itemsMapper.isFavourite(id, UserContext.getCurrentUserId());
        if (is != null && id != 0) {
            goodsById.setFavourite(true);
        }
        return goodsById;
    }

    //根据关键词查询相关商品信息
    @Override
    public PageResult<GoodsVO> selectByKeyword(String keyword) {
        ParamUtil.notBlank(keyword, "搜索关键词");
        List<GoodsVO> goodsList = itemsMapper.selectByKeyword(keyword, UserContext.getCurrentUserId());
        return new PageResult<>((long) goodsList.size(), goodsList);
    }

    //查找用户发布的商品
    @Override
    public PageResult<GoodsVO> selectMyGoods() {
        List<GoodsVO> goodsList = itemsMapper.selectByUserId(UserContext.getCurrentUserId());
        return new PageResult<>((long) goodsList.size(), goodsList);
    }

    //商品评论
    @Override
    public void addItemComment(CommentGoods comment) {
        ParamUtil.notNull(comment, "评论信息");
        ParamUtil.positive(comment.getGoodsId(), "商品id");
        ParamUtil.notBlank(comment.getText(), "评论内容");
        comment.setUserId(UserContext.getCurrentUserId());
        comment.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        itemsMapper.addCommentItem(comment);
    }

    //分页查询商品评论
    @Override
    public PageResult<CommentGoodsVO> selectItemComment(Long goodsId) {
        ParamUtil.positive(goodsId, "商品id");
        try (Page<CommentGoodsVO> page = PageHelper.startPage(1, 10)) {
            List<CommentGoodsVO> commentList = itemsMapper.selectItemComment(goodsId);
            return new PageResult<>(page.getTotal(), commentList);
        }
    }

    //查看该评论之前的所有互动
    @Override
    public PageResult<CommentGoodsVO> selectItemCommentInteraction(Long id) {
        ParamUtil.positive(id, "评论id");
        try (Page<CommentGoodsVO> page = PageHelper.startPage(1, 10)) {
            List<CommentGoodsVO> commentList = itemsMapper.selectItemCommentInteraction(id);
            return new PageResult<>(page.getTotal(), commentList);
        }
    }

    //根据传递的商品id查询商品的简略信息，以实现会话列表的商品信息查询b
    @Override
    public ProductAssociationVO selectBriefInfo(Long goodsId) {
        return itemsMapper.selectBriefInfo(goodsId);
    }


    //我已经卖出的商品
    @Override
    public PageResult<GoodsVO> selectMySold(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
        try (Page<GoodsVO> page = PageHelper.startPage(itemQueryParam.getPageNumber(), 10)) {
            List<GoodsVO> list = itemsMapper.selectMySold(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), list);
        }
    }

    //我已经购买的商品
    @Override
    public PageResult<GoodsVO> selectMyPurchase(ItemQueryParam itemQueryParam) {
        ParamUtil.checkPage(itemQueryParam);
        try (Page<GoodsVO> page = PageHelper.startPage(itemQueryParam.getPageNumber(), 10)) {
            List<GoodsVO> list = itemsMapper.selectMyPurchase(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), list);
        }
    }

    //评价交易对方
    @Override
    @Transactional
    public Boolean evaluate(EvaluateDTO dto) {
        ParamUtil.notNull(dto, "评价信息");
        ParamUtil.positive(dto.getGoodsId(), "商品id");
        ParamUtil.notBlank(dto.getContent(), "评价内容");
        if (dto.getScore() == null || dto.getScore() < 1 || dto.getScore() > 5) {
            throw new BusinessException("评分必须在1到5之间");
        }
        Long currentUserId = UserContext.getCurrentUserId();
        //根据商品id和当前用户id得到交易对方的id（对方是卖家则返回买家，是买家则返回卖家）
        Long oppositeId = userMapper.getOppositeId(currentUserId, dto.getGoodsId());
        //对方id为空或为0，说明当前用户不是该商品的交易双方，或者交易尚未完成
        if (oppositeId == null || oppositeId == 0L) {
            throw new BusinessException("仅交易双方可评价");
        }
        Evaluate evaluate = new Evaluate();
        evaluate.setEvaluatorId(currentUserId);//评价人：当前登录用户
        evaluate.setEvaluatedId(oppositeId);//被评价人：交易对方
        evaluate.setContent(dto.getContent());
        evaluate.setScore(dto.getScore());
        evaluate.setGoodsId(dto.getGoodsId());
        evaluate.setCreateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
        return itemsMapper.insertEvaluate(evaluate) > 0;
    }
}