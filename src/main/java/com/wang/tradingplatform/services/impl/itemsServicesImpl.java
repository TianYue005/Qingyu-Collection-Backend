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
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.itemsServices;
import com.wang.tradingplatform.utils.ListUtil;
import com.wang.tradingplatform.utils.ParamUtil;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import com.wang.tradingplatform.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class itemsServicesImpl implements itemsServices {

    private final UserMapper userMapper;
    private final ItemsMapper itemsMapper;
    private final SnowflakeIdUtil snowflakeIdUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> initDataScript;
    private final ObjectMapper objectMapper;

    private final Set<String> setCategory = new HashSet<>(Set.of("电子产品", "饰品", "家居", "未知", "全部"));
    private final Map<Integer, String> setTag = new HashMap<>(Map.ofEntries(
            Map.entry(0, "全新未拆"),
            Map.entry(1, "仅拆封"),
            Map.entry(2, "轻微使用痕迹"),
            Map.entry(3, "配件齐全"),
            Map.entry(4, "保修期内"),
            Map.entry(5, "支持验货"),
            Map.entry(6, "不议价"),
            Map.entry(7, "可换"),
            Map.entry(8, "毕业出"),
            Map.entry(9, "急用钱"),
            Map.entry(10, "礼盒装")
    ));

    //添加商品
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(UploadItemDTO uploadItemDTO) {
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
        if (uploadItemDTO.getSpecs() == null || uploadItemDTO.getSpecs().length == 0) {
            throw new BusinessException("商品规格不能为空");
        }
        if (uploadItemDTO.getOriginalPrice() != null && uploadItemDTO.getOriginalPrice().compareTo(maxPrice) > 0) {
            throw new BusinessException("原价不能超过 99999999.99 元");
        }
        HashSet<String> hSet = new HashSet<>(List.of(uploadItemDTO.getSpecs()));
        //校验商品规格是否属于合法分类
        if (!ListUtil.ListAIfListB(hSet, setCategory)) {
            throw new BusinessException("商品规格不合法");
        }
        HashSet<String> hSet2 = new HashSet<>(List.of(uploadItemDTO.getTag()));
        Integer l = ListUtil.ListAIfListB(hSet2, setTag);
        if (l == null) {
            throw new BusinessException("商品属性不合法");
        }
        Long goodsId = snowflakeIdUtil.nextId();
        uploadItemDTO.setUserId(UserContext.getCurrentUserId());
        uploadItemDTO.setGoodsId(goodsId);
        uploadItemDTO.setIntTag(l);
        //将商品基本信息存储
        itemsMapper.insert(uploadItemDTO);
        //存储商品tag信息
        itemsMapper.insertTagInformation(uploadItemDTO);
        //插入商品图片（含宽高）
        Picture[] images = uploadItemDTO.getImage();
        if (images != null && images.length > 0) {
            List<GoodsImage> goodsImages = Arrays.stream(images)
                    .map(pic -> {
                        GoodsImage goodsImage = new GoodsImage();
                        goodsImage.setGoodsId(goodsId);
                        goodsImage.setImgUrl(pic.getUrl());
                        goodsImage.setImgWidth(pic.getWidth());
                        goodsImage.setImgHeight(pic.getHeight());
                        return goodsImage;
                    })
                    .toList();
            itemsMapper.insertImages(goodsImages);
        }
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
            List<GoodsVO> goodsList = itemsMapper.page(UserContext.getCurrentUserId());
            //构造并返回分页结果对象，包含总记录数和当前页数据
            return new PageResult<>(page.getTotal(), goodsList);
        }
    }

    //根据商品ID查询商品信息
    @Override
    public GoodsVO findGoodsById(Long id) {
        ParamUtil.positive(id, "商品id");
        GoodsVO goodsById = itemsMapper.findGoodsById(id);
        //查看某商品是否被收藏
        Integer is = itemsMapper.isFavourite(id, UserContext.getCurrentUserId());
        if (is != null) {
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
    @Transactional(rollbackFor = Exception.class)
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

    //商品推荐
    @Override
    public Result<List<GoodsVO>> Recommend() {
        // 推荐位数据已整体缓存在 Redis（ZSet 成员是商品详情JSON），直接读取，无需再查MySQL
        Set<String> jsonList = stringRedisTemplate.opsForZSet().range("goods:recommend", 0, 4);
        if (jsonList == null || jsonList.isEmpty()) {
            throw new RuntimeException("数据不足");
        }
        List<GoodsVO> goodsList = jsonList.stream()
                .map(this::parseGoods)
                .filter(Objects::nonNull)
                .toList();
        return Result.success(goodsList);
    }

    /**
     * 将自己的商品购买推荐位
     * 脚本功能：先把同一个商品的旧数据删掉 → 看看推荐位前 5 个位置满没满 → 没满就把最新商品详情写进去 → 返回还剩几个空位。
     * todo 建立在redis有持久化存储 比如 RDB
     */
    @Override
    public Result<String> RecommendedSpot(Long goodsId) {
        ParamUtil.positive(goodsId, "商品id");
        // 从 MySQL 校验商品是否可推荐，并获取最新详情（权威数据源）
        List<GoodsVO> recommend = itemsMapper.getRecommend(Set.of(goodsId));
        if (recommend == null || recommend.isEmpty()) {
            return Result.error("商品不存在或已售出，无法推荐");
        }
        String goodsJson = toJson(recommend.getFirst());
        // 第一次：Lua 原子完成“删旧条目 + 检查空位 + 写入最新详情”
        Long remain = stringRedisTemplate.execute(
                initDataScript,
                List.of("goods:recommend"),
                goodsId.toString(),
                goodsJson,
                "99");
        if (remain == null || remain < 0) {
            return Result.error("空位不够啦，以后再来吧");
        }
        // 延迟双删：500ms 后再执行一次“删旧写新”，清理并发窗口内可能写入的旧快照，保证最终一致
        CompletableFuture.runAsync(
                () -> stringRedisTemplate.execute(
                        initDataScript,
                        List.of("goods:recommend"),
                        goodsId.toString(),
                        goodsJson,
                        "99"),
                CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS));
        return Result.success("添加成功");
    }

    // 将商品详情序列化为JSON，用于写入Redis缓存
    private String toJson(GoodsVO goods) {
        try {
            return objectMapper.writeValueAsString(goods);
        } catch (JacksonException e) {
            throw new RuntimeException("商品信息序列化失败", e);
        }
    }

    // 将Redis中的JSON反序列化为商品详情，脏数据解析失败返回null由调用方过滤
    private GoodsVO parseGoods(String json) {
        try {
            return objectMapper.readValue(json, GoodsVO.class);
        } catch (JacksonException e) {
            return null;
        }
    }
}