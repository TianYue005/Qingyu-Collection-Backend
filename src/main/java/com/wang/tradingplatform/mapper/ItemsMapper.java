package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.GoodsImage;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Mapper
public interface ItemsMapper {
    //上架商品
    int insert(Goods items);

    //根据ID查找商品
    List<GoodsVO> selectById(Long id);

    //分页查询
    List<GoodsVO> page(ItemQueryParam itemQueryParam);

    //插入商品图片
    int insertImage(GoodsImage goodsImage);

    //根据商品ID查询图片列表
    List<GoodsImage> selectImagesByGoodsId(Long goodsId);

    //根据商品ID查询商品信息
    GoodsVO findGoodsById(Long id);

    //根据关键词查询相关商品信息
    List<GoodsVO> selectByKeyword(String keyword);

    //查找用户发布的商品

}
