package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.CommentGoods;
import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.ProductAssociationVO;
import com.wang.tradingplatform.pojo.vo.*;
import com.wang.tradingplatform.services.impl.itemsServicesImpl;
import com.wang.tradingplatform.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品上架模块")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemsController {

    private final itemsServicesImpl itemServices;

    //商品上架
    @Operation(summary = "商品上架")
    @PostMapping("/add")
    public Result<String> add(@RequestBody UploadItemDTO uploadItemDTO) {
        log.info("========== 添加商品 ==========");
        Boolean b = itemServices.add(uploadItemDTO);
        if (b) {
            return Result.success();
        }
        return Result.error("商品上架失败");
    }

    //根据ID查找商品
    @Operation(summary = "根据ID查找商品")
    @GetMapping("/{id}")
    public Result<GoodsVO> findById(@PathVariable Long id) {
        log.info("========== 查找商品, id: {} ==========", id);
        GoodsVO item = itemServices.findGoodsById(id);
        if (item == null) {
            return Result.error("商品不存在");
        }
        return Result.success(item);
    }

    //分页查询
    @Operation(summary = "分页查询")
    @GetMapping("/select")
    public Result<PageResult<GoodsVO>> selectToPage(ItemQueryParam itemQueryParam) {
        log.info("========== 分页查询 ==========");
        PageResult<GoodsVO> goods = itemServices.toPage(itemQueryParam);
        return Result.success(goods);
    }

    //关键词查询
    @Operation(summary = "关键词查询")
    @GetMapping("/search")
    public Result<PageResult<GoodsVO>> selectByKeyword(@RequestParam String keyword) {
        log.info("========== 关键词查询 ==========");
        PageResult<GoodsVO> goods = itemServices.selectByKeyword(keyword);
        return Result.success(goods);
    }

    //查询用户发布的商品
    @Operation(summary = "查询用户发布的商品")
    @GetMapping("/myItems")
    public Result<PageResult<GoodsVO>> myItems() {
        log.info("========== 查询用户发布的商品 ==========");
        PageResult<GoodsVO> goods = itemServices.selectMyGoods();
        return Result.success(goods);
    }

    /*商品评论相关*/
    //商品评论
    @Operation(summary = "商品评论")
    @PostMapping("/add/comment/item")
    public Result<CommentGoods> addItemComment(@RequestBody CommentGoods comment) {
        itemServices.addItemComment(comment);
        return Result.success();
    }

    //查看商品评论
    @Operation(summary = "查看商品评论")
    @GetMapping("/select/comment/item")
    public Result<PageResult<CommentGoodsVO>> selectItemComment(@RequestParam Long goodsId) {
        PageResult<CommentGoodsVO> page = itemServices.selectItemComment(goodsId);
        return Result.success(page);
    }

    //查看该评论之前的所有互动
    @Operation(summary = "查看该评论之前的所有互动")
    @GetMapping("/select/comment/item/{id}")
    public Result<PageResult<CommentGoodsVO>> selectItemCommentInteraction(@PathVariable Long id) {
        PageResult<CommentGoodsVO> page = itemServices.selectItemCommentInteraction(id);
        return Result.success(page);
    }

    //信用及评价 todo
    @Operation(summary = "查看用户的评价以及别人对用户的评价")
    @GetMapping("/select/comment")
    public void selectComment(@RequestBody ItemQueryParam itemQueryParam) {
        if (itemQueryParam.getTrait() == 3) {
            //说明是在查询来自卖家对该用户的评价
        } else {
            //说明是该用户对交易的评价
        }
    }

    //根据传递的商品id查询商品的简略信息，以实现会话列表的商品信息查询
    @Operation(summary = "根据传递的商品id查询商品的简略信息，以实现会话列表的商品信息查询")
    @GetMapping("/select/briefInfo/{goodsId}")
    public ProductAssociationVO selectBriefInfo(@PathVariable Long goodsId) {
        return itemServices.selectBriefInfo(goodsId);
    }


    //我已经卖出的商品
    @Operation(summary = "我已经卖出的商品")
    @GetMapping("/select/mySold")
    public PageResult<GoodsVO> selectMySold(ItemQueryParam itemQueryParam) {
        return itemServices.selectMySold(itemQueryParam);
    }

    //我已经购买的商品
    @Operation(summary = "我已经购买的商品")
    @GetMapping("/select/myPurchase")
    public PageResult<GoodsVO> selectMyPurchase(ItemQueryParam itemQueryParam) {
        return itemServices.selectMyPurchase(itemQueryParam);
    }
}
