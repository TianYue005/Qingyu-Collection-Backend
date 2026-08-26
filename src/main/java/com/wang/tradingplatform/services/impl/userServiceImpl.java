package com.wang.tradingplatform.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wang.tradingplatform.annotation.Permission;
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

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class userServiceImpl implements userService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;
    private final userUtil userUtil;
    private final SnowflakeIdUtil snowflakeIdUtil;
    private final RedisUtil redisUtil;
    private final ItemsMapper itemsMapper;

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
    @Permission
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
    @Permission
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
    @Permission
    public User selectAccountAndName(Long userId) {
        return userMapper.selectAccountAndName(userId);
    }


    /**
     *
     * @return 会话列表信息
     */
    @Override
    @Permission
    public List<ChatMessageListVO> selectChatList() {
        Long id = UserContext.getCurrentUserId();
        List<ChatMessageListVO> list= userMapper.selectChatList(id);
        System.out.println("------------------------------------------");
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
    @Permission
    public PageResult<ChatMessageListVO> selectHistory(ItemQueryParam itemQueryParam) {
        //查找该用户的所有有关联的sessionId
        List<Long> sessionIdList = userMapper.selectUserSessionList(UserContext.getCurrentUserId());
        //判断传递的sessionId是否真的属于该用户
        if (sessionIdList.contains(itemQueryParam.getSessionId())) {
            //该用户传递的sessionId确实是该用户的
            try (Page<ChatMessageListVO> page = PageHelper.startPage(
                    itemQueryParam.getPageNumber(),
                    itemQueryParam.getPageSize()
            )) {
                List<ChatMessageListVO> list1 = userMapper.selectUserChatList(List.of(itemQueryParam.getSessionId()));
                return new PageResult<ChatMessageListVO>(page.getTotal(), list1);
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
    @Permission
    public int favourite(Long id) {
        return userMapper.addFavourite(UserContext.getCurrentUserId(), id);
    }

    /**
     * 查看收藏功能
     *
     * @return
     */
    @Override
    @Permission
    public PageResult<GoodsVO> selectFavourite(ItemQueryParam itemQueryParam) {
        //使用PageHelper进行分页处理（try-with-resources确保ThreadLocal资源被清理）
        try (Page<Goods> page = PageHelper.startPage(
                itemQueryParam.getPageNumber(),
                itemQueryParam.getPageSize(),
                itemQueryParam.getSortRules())) {
            //调用mapper接口执行查询 查到的数据是没有图片的
            List<GoodsVO> goodsList = itemsMapper.selectFavourite(itemQueryParam, UserContext.getCurrentUserId());
            if (goodsList.isEmpty()) {
                return new PageResult<GoodsVO>(page.getTotal(), Collections.emptyList());
            }
            // 2. 提取所有的 goodsId
            List<Long> goodsIds = goodsList.stream()
                    .map(GoodsVO::getGoodsId)
                    .toList();
            // 3. 批量查询图片并按 goodsId 分组
            List<GoodsImage> images = itemsMapper.selectImagesByGoodsIds(goodsIds);
            Map<Long, List<GoodsImage>> imageMap = images.stream()
                    .collect(Collectors.groupingBy(GoodsImage::getGoodsId));
            // 4. 回填图片到商品列表中
            for (GoodsVO goods : goodsList) {//这里的goodsList是没有图片数据的
                goods.setImgList(imageMap.getOrDefault(goods.getGoodsId(), Collections.emptyList()));
                /*
                根据循环到的goods.getGoodsId()得到对应的List<GoodsImage>
                getOrDefault是得到或者默认值，即要么根据第一个参数goods.getGoodsId()得到想要的内容
                否则得到一个准备好的默认值Collections.emptyList()
                */
            }
            //构造并返回分页结果对象，包含总记录数和当前页数据
            return new PageResult<GoodsVO>(page.getTotal(), goodsList);
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
    @Override
    public Long createChatSession(Long toUserId) {
        SnowflakeIdUtil util = new SnowflakeIdUtil();
        long sessionId = 0L;
        Long CurrentUserId = UserContext.getCurrentUserId();
        //先看现在双方是否有会话
        Long sessionHistory = userMapper.selectSessionHistory(toUserId, CurrentUserId);
        if (sessionHistory == null) {
            //说明没有历史会话
            sessionId = util.nextId();

            SessionState Session = new SessionState();
            Session.setGroupId(0L);//设置私聊
            Session.setSessionId(sessionId);//sessionId
            Session.setFromUid(CurrentUserId);//发送用户的id
            Session.setToUid(toUserId);//接受用户的id
            Session.setCreateTime(LocalDateTime.now());//会话创建时间
            Session.setIsRead(0L);//设为未读 TODO先这个样子 忘了之前怎么想的了

            userMapper.createChatSession(Session);
            sessionHistory = userMapper.selectSessionHistory(toUserId, CurrentUserId);
        }
        return sessionHistory;
    }
}
