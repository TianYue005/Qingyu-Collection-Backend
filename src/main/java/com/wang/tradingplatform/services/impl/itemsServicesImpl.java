package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.mapper.ItemsMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.GoodsImage;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.Picture;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.itemsServices;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import com.wang.tradingplatform.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        goods.setUserId(userMapper.findIDByAccount(UserContext.getCurrentAccount()));
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
        //使用PageHelper进行分页处理（try-with-resources确保ThreadLocal资源被清理）
        try (Page<Goods> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            //调用mapper接口执行查询 查到的数据是没有图片的
            List<GoodsVO> goodsList = itemsMapper.page(itemQueryParam);
            //构造并返回分页结果对象，包含总记录数和当前页数据
            return new PageResult<GoodsVO>(page.getTotal(), goodsList);
        }
    }

    //根据商品ID查询商品信息
    @Override
    public GoodsVO findGoodsById(Long id) {
        return itemsMapper.findGoodsById(id);
    }

    //根据关键词查询相关商品信息
    @Override
    public PageResult<GoodsVO> selectByKeyword(String keyword) {
        List<GoodsVO> goodsList = itemsMapper.selectByKeyword(keyword);
        return new PageResult<>((long) goodsList.size(), goodsList);
    }

    //查找用户发布的商品 todo
    @Override
    public PageResult<GoodsVO> selectMyGoods() {
        UserContext.getCurrentAccount();
        return null;
    }
}