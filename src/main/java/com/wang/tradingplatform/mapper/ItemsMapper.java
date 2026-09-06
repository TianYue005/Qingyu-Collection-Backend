package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentGoodsVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ItemsMapper {
    //上架商品
    int insert(Goods items);

    //根据商品ID查找商品
    List<GoodsVO> selectById(Long id);

    //分页查询
    List<GoodsVO> page(ItemQueryParam itemQueryParam,Long userId);

    //插入商品图片
    int insertImage(GoodsImage goodsImage);

    //根据商品ID查询图片列表
    List<GoodsImage> selectImagesByGoodsId(Long goodsId);

    //根据商品ID查询商品信息
    GoodsVO findGoodsById(Long id);

    //根据关键词查询相关商品信息
    List<GoodsVO> selectByKeyword(String keyword,Long userId);

    //根据用户id查找用户发布的商品
    List<GoodsVO> selectByUserId(Long currentUserId);

    //根据传递的商品id列表查询对应的商品图片信息
    List<GoodsImage> selectImagesByGoodsIds(List<Long> goodsIds);

    //查到的数据是没有图片的
    List<GoodsVO> selectFavourite(@Param("param") ItemQueryParam itemQueryParam,@Param("currentUserId") Long currentUserId);

    //商品评论
    void addCommentItem( CommentGoods comment);

    //查看商品评论
    List<CommentGoodsVO> selectItemComment(Long goodsId);

    //查看该评论之前的所有互动
    List<CommentGoodsVO> selectItemCommentInteraction(Long commentId);

    //查看某商品是否被收藏
    Integer isFavourite(Long id, Long currentUserId);

    //根据传递的商品id查询商品的简略信息，以实现会话列表的商品信息查询b
    ProductAssociationVO selectBriefInfo(Long goodsId);
}
