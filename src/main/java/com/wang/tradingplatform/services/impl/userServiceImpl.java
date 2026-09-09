package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.mapper.ItemsMapper;
import com.wang.tradingplatform.mapper.UserMapper;
import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.ChatMessageListVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class userServiceImpl implements userService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;
    private final userUtil userUtil;
    private final SnowflakeIdUtil snowflakeIdUtil;
    private final RedisUtil redisUtil;
    private final ItemsMapper itemsMapper;
    private final ChatServiceImpl chatService;

    /**
     * 注册功能
     *
     * @param registerDTO 注册请求参数
     * @return
     */
    @Override
    public String register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername(); // 用户名
        String account = registerDTO.getAccount(); // 手机号
        String password = registerDTO.getPassword(); // 密码
        boolean b = userUtil.checkRegisterDTO(registerDTO);
        if (!b) {
            return "输入信息有误";
        }
        // 构建用户对象
        User user = new User();
        user.setUserId(snowflakeIdUtil.nextId());
        user.setUserName(username);
        user.setAccount(account);
        // TODO: 密码目前明文存储
        user.setPassword(password);
        user.setStatus(1);                          // 1-正常
        user.setBalance(BigDecimal.ZERO);           // 初始余额0
        user.setIntegral(100);                      // 初始活跃度100
        user.setCredit(80);                         // 初始信誉分80
        user.setLevel(1);                           // 初始等级1
        user.setDeleted(0);                     // 未删除
        user.setAvatar("默认头像");
        // 插入数据库
        int rows = userMapper.insert(user);
        if (rows > 0) {
            return "注册成功";
        }
        return "注册失败";
    }

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return
     */
    @Override
    public String login(LoginDTO loginDTO) {
        Long uId = userMapper.findIDByAccount(loginDTO.getAccount());
        //检验loginDTO的数据
        boolean b = userUtil.checkLoginDTO(loginDTO);
        if (!b) {
            return "信息有误";
        }
        //登录
        Integer login = userMapper.login(loginDTO.getAccount(), loginDTO.getPassword());
        if (login == null || login == 0) {
            return "登陆失败，请检查账号或者密码";
        }
        //生成token,并返回
        String token = jwtTokenUtil.generateToken(uId);
        redisUtil.set(String.valueOf(uId), token);//向redis中存储用户的token
        return token;
    }

    /**
     * 根据用户账号查找对应的用户名
     *
     * @param account
     * @return
     */
    @Override
    public String selectName(String account) {
        return userMapper.selectName(account);
    }

    /**
     * 根据用户账号查询用户Id
     *
     * @param account
     * @return
     */
    @Override
    public Long selectId(String account) {
        return userMapper.findIDByAccount(account);
    }

    /**
     * 根据用户Id查询对应的用户名与账号
     *
     * @param userId
     * @return
     */
    @Override
    public User selectAccountAndName(Long userId) {
        return userMapper.selectAccountAndName(userId);
    }


    /**
     *
     * @return 会话列表信息
     */
    @Override
    public List<ChatMessageListVO> selectChatList() {
        Long id = UserContext.getCurrentUserId();
        List<ChatMessageListVO> list = userMapper.selectChatList(id);
        System.out.println("------------------会话列表信息------------------------");
        System.out.println(list);
        return list;
    }

    /**
     * 根据传递的sessionId获取历史消息
     *
     * @param itemQueryParam
     * @return
     */
    @Override
    public PageResult<ChatMessage> selectHistory(ItemQueryParam itemQueryParam) {
        //查找该用户的所有有关联的sessionId
        List<Long> sessionIdList = userMapper.selectUserSessionList(UserContext.getCurrentUserId());
        //判断传递的sessionId是否真的属于该用户
        if (sessionIdList.contains(itemQueryParam.getSessionId())) {
            //该用户传递的sessionId确实是该用户的
            List<ChatMessage> chatMessages = chatService.getPrivateHistoryRedis(itemQueryParam);
            //如果redis查到的数据足够则直接返回redis的数据
            if (chatMessages.size() == itemQueryParam.getPageSize()) {
                //要求redis查到的数据足够要求的数目
                return new PageResult<ChatMessage>((long) itemQueryParam.getPageSize(), chatMessages);
            }
            //redis的数据不够则查看mysql的数据
            try (Page<ChatMessage> page = PageHelper.startPage(
                    itemQueryParam.getPageNumber(),
                    itemQueryParam.getPageSize()
            )) {
                List<ChatMessage> list1 = userMapper.selectUserChatList(itemQueryParam.getSessionId());
                return new PageResult<ChatMessage>(page.getTotal(), list1);
            }
        }
        //根据结果返回信息
        return new PageResult<>(0L, List.of());
    }

    /**
     * 添加收藏功能
     *
     * @param id
     * @return
     */
    @Override
    public int favourite(Long id) {
        return userMapper.addFavourite(UserContext.getCurrentUserId(), id);
    }

    /**
     * 查看收藏功能
     *
     * @return
     */
    @Override
    public PageResult<GoodsVO> selectFavourite(ItemQueryParam itemQueryParam) {
        try (Page<Goods> page = PageHelper.startPage(itemQueryParam.getPageNumber(), itemQueryParam.getPageSize())) {
            List<GoodsVO> list = userMapper.selectFavourite(UserContext.getCurrentUserId());
            return new PageResult<>(page.getTotal(), list);
        }
    }

    /**
     * 得到账号基本信息
     *
     * @param account
     * @return
     */
    @Override
    public User selectAccountInfo(String account) {
        return userMapper.selectAccountInfo(UserContext.getCurrentUserId());
    }

    /**
     * 修改密码
     *
     * @param password
     * @return
     */
    @Override
    public void updatePassword(String password) {
        userMapper.updateUserPassword(password, UserContext.getCurrentUserId());
    }

    //取消收藏功能
    @Override
    public Integer favouriteRM(Long id) {
        return userMapper.favouriteRM(id, UserContext.getCurrentUserId());
    }

    //用户点击了发起会话的按钮

    /**
     * 不仅在数据库添加了会话数据
     * 也为会话的关联表设置了相关商品字段（如果传递了的话）
     *
     * @param toUserId
     * @param goodsId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChatSession(Long toUserId, Long goodsId) {
        if (Objects.equals(toUserId, UserContext.getCurrentUserId())) {
            throw new RuntimeException("不允许与自己发起会话");
        }

        SnowflakeIdUtil util = new SnowflakeIdUtil();
        long sessionId = 0L;
        Long CurrentUserId = UserContext.getCurrentUserId();
        //先看现在双方是否有会话
        //如果有会话就返回会话的sessionId
        Long sessionHistory = userMapper.selectSessionHistory(toUserId, CurrentUserId);
        if (sessionHistory == null) {
            //说明没有历史会话
            sessionId = util.nextId();//此处得到将来要设置的sessionId

            SessionState Session = new SessionState();
            Session.setGroupId(0L);//设置私聊
            Session.setSessionId(sessionId);//sessionId
            Session.setFromUid(CurrentUserId);//发送用户的id
            Session.setToUid(toUserId);//接受用户的id
            Session.setCreateTime(LocalDateTime.now());//会话创建时间
            Session.setIsRead(0L);//设为未读 TODO先这个样子 忘了之前怎么计划的了

            userMapper.createChatSession(Session);
            //为两个用户的会话设置最新的相关商品状态
            userMapper.createSessionToGoods(sessionId, goodsId);
            sessionHistory = userMapper.selectSessionHistory(toUserId, CurrentUserId);
        }
        //为两个用户的会话设置最新的相关商品状态
        userMapper.createSessionToGoods(sessionHistory, goodsId);
        return sessionHistory;
    }

    //的到与当前用户对话的用户的id
    @Override
    public String getOtherId(Long sessionId, Long currentUserId) {
        return String.valueOf(userMapper.getOtherId(sessionId, currentUserId));
    }

    /**
     * 会话商品联想
     * 前端传递session_id后端根据session_id查看与它相关的商品简略信息并返回
     * 只查询与查询当前时间相差1天内最新的那一条消息
     *
     * @return 返回的是商品部分信息 商品名，商品图片其中的一张，商品id，商品价格
     */
    @Override
    public ProductAssociationVO tradeRequestLenovo(Long sessionId) {
        System.out.println("会话商品联想传递的sessionid" + sessionId);
        ProductAssociationVO g = userMapper.tradeRequestLenovo(sessionId);
        if (g == null) {
            System.out.println("商品联想内容为空");
        }
        return g;
    }


    /**
     * 查询交易信息以及交易状态
     * 前端传递商品id，返回商品简略信息以及交易状态
     * 0未处理 1已接受 2已拒绝
     */
    @Override
    public ProductAssociationVO selectTradeState(Long goodsId, Long session_id) {
        String redisKey = "TradeState:" + session_id + goodsId;
        ProductAssociationVO vo = (ProductAssociationVO) redisUtil.get(redisKey);
        if (vo != null) {
            //如果redis返回了数据则直接返回
            System.out.println("redis有数据");
            return vo;
        }
        redisUtil.set(redisKey, vo);
        System.out.println("redis无数据，从mysql获取");
        vo = userMapper.selectTradeInfo(goodsId, session_id);
        return vo;
    }

    //拒绝或者接受交易请求
    //交易状态 0 未确认 1 已有请求 2已同意请求 3已拒绝
    @Override
    public void HandleTradeRequest(Integer select, Long goods_id, Long session_id) {
        userMapper.HandleTradeRequest(select, goods_id, session_id);
    }

    //先看该用户是否有权利拒绝或者同意
    @Override
    public Integer getPermission(Long currentUserId, Long goodsId) {
        return userMapper.getPermission(currentUserId, goodsId);
    }

    //同步goods表的购买人id
    @Override
    public void saleGoods(Long goodsId, Long toUid, Long currentUid) {
        userMapper.updateGoodsSold(goodsId, toUid, currentUid);
    }

    //当前用户的待处理交易
    @Override
    public List<Pending> userPending(Long userId) {
        List<Pending> List1 = new ArrayList<>(userMapper.userPending(userId));
        List<Pending> List2 = new ArrayList<>(userMapper.userPending2(userId));
        log.error(List1.toString());
        log.info("-------------------------------------------");
        log.error(List2.toString());
        List<Pending> list = new ArrayList<>(List1);
        list.addAll(List2);
        log.error(list.toString());
        return list;
    }

    //填写别人的验证码  自己是第一个则返回1 自己是第二个则返回2 其他都为错误
    @Override
    public Integer putOtherVerifyCode(TradePairUp tradePairUp) {
        //先看自己之前是不是有人填写过了，如果没有就填写。如果有就进行下一步
        //计算出合理的key
        Long myId = UserContext.getCurrentUserId();
        Long otherId = getOppositeId(myId, tradePairUp.getGoodsId());
        String key = "MyVerifyCode" + otherId + myId;
        log.info("========== 填写验证码04 ==========");
        Object code = redisUtil.get(key);
        if (code != null && code.equals(tradePairUp.getOtherVerifyCode())) {
            log.info("========== 填写验证码01 ==========");
            //传递的验证码确实是与对方契合
            String verifyKey = "Verify" + tradePairUp.getGoodsId();
            Integer flag = (Integer) redisUtil.get(verifyKey);
            if (flag != null && flag == 1) {
                log.info("========== 填写验证码02 ==========");
                //对方已经填写过一次了，自己是第二个 此时应该完成交易并返回
                userMapper.FinishTrade(tradePairUp.getGoodsId());
                return 2;
            } else {
                //说明自己是第一个  此时应该开始仪式
                log.info("========== 填写验证码03 ==========");
                redisUtil.set(verifyKey, 1, 5, TimeUnit.MINUTES);
                return 1;
            }
        }
        return 3;
    }

    //根据自己的id与商品id得到对面的id
    @Override
    public Long getOppositeId(Long myId, Long goodsId) {
        Long id = userMapper.getOppositeId(myId, goodsId);
        if (id != null) {
            return id;
        }
        return myId;
    }
}
