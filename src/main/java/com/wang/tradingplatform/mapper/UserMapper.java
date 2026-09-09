package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.ChatMessageListVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 登录 传递密码和账号 能查到就可以登陆
     */
    Integer login(String account, String password);

    /**
     * 根据账号查找用户名字
     */
    String selectName(String string);


    /**
     * 根据账号查询用户id
     */
    @Select("SELECT USER_ID FROM user WHERE account = #{account} AND deleted = 0")
    Long findIDByAccount(@Param("account") String account);

    /**
     * 根据用户id查询账号和名字
     *
     * @param userId
     * @return
     */
    User selectAccountAndName(Long userId);

    /**
     * 根据用户id查询用户名
     */
    String selectUserNameById(Long id);


    /**
     * 根据session的id列表查找对应的聊天信息 并返回列表数据
     *
     * @param sessionId
     * @return
     */
    List<ChatMessage> selectUserChatList(Long sessionId);

    /**
     * 添加收藏功能
     *
     * @return
     */
    int addFavourite(@Param("userId") Long userId, @Param("itemId") Long itemId);

    /**
     * 查看收藏功能
     *
     * @param currentUserId 用户的Id
     * @return 返回的是用户收藏的商品的 商品id
     */
    List<GoodsVO> selectFavourite(Long currentUserId);

    /**
     * 得到账号基本信息
     *
     * @param currentUserId 用户的Id
     * @return User
     */
    User selectAccountInfo(Long currentUserId);

    /**
     * 修改密码
     *
     * @param password
     * @param currentUserId
     */
    void updateUserPassword(String password, Long currentUserId);

    //取消收藏功能
    Integer favouriteRM(Long id, Long userId);

    //看现在双方是否有会话(有的话直接返回会话的sessionId)
    Long selectSessionHistory(@Param("toUid") Long toUserId, @Param("fromUid") Long currentUserId);

    //发起会话
    Long createChatSession(SessionState Session);

    //分别查询自己与别人对自己发起的聊天
    List<ChatMessageListVO> selectChatList(Long currentUserId);

    //查找该用户的所有有关联的sessionId
    List<Long> selectUserSessionList(Long currentUserId);

    //查找请求用户有关的所有点对点对话的另一方的id的列表。
    List<Long> selectToId(Long id);

    //根据求到的id列表查询最后一条消息
    List<ChatMessageListVO> selectEndMessage(List<Long> idList);

    //的到与当前用户对话的用户的id
    Long getOtherId(Long sessionId, Long currentUserId);

    //为两个用户的会话设置最新的相关商品状态
    void createSessionToGoods(@Param("sessionHistory") Long sessionHistory, @Param("goodsId") Long goodsId);

    //会话商品联想
    //前端传递session_id后端根据session_id查看与它相关的商品简略信息并返回
    //只查询与查询当前时间相差1天内最新的那一条消息
    ProductAssociationVO tradeRequestLenovo(Long sessionId);

    //查询商品相关信息
    ProductAssociationVO selectGoodsInfoById(Long goodsId);

    //查询交易信息以及交易状态
    ProductAssociationVO selectTradeInfo(Long goodsId, Long sessionId);

    //拒绝或者接受交易请求
    //交易状态 0 未确认 1 已有请求 2已同意请求 3已拒绝
    void HandleTradeRequest(@Param("select") Integer select, @Param("goodsId") Long goods_id, @Param("sessionId") Long session_id);

    //存储交易这一请求到数据库
    Integer saveTradeRequest(ChatMessage chatMessage);

    //先看该用户是否有权利拒绝或者同意
    Integer getPermission(Long currentUserId, Long goodsId);

    //同步goods表的购买人id
    void updateGoodsSold(Long goodsId, Long toUid, Long currentUid);

    //当前用户的待处理交易
    List<Pending> userPending(Long userId);

    //根据自己的id与商品id得到对面的id
    Long getOppositeId(Long myId, Long goodsId);

    //完成交易并返回
    void FinishTrade(Long goodsId);

    //当前用户的待处理交易2
    List<Pending> userPending2(Long userId);
}
