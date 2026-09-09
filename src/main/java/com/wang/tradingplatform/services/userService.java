package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.ChatMessageListVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface userService {
    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果消息
     */
    String register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return
     */
    String login(LoginDTO loginDTO);

    /**
     * 根据用户账号查询用户名
     */
    String selectName(String string);

    /**
     * 根据用户账户号查用户Id
     */
    Long selectId(String account);

    /**
     * 根据用户Id查询对应的用户名与账号
     */
    User selectAccountAndName(Long userId);

    /**
     * 返回用户聊天列表
     *
     * @return
     */
    List<ChatMessageListVO> selectChatList();

    /**
     * 根据传递的sessionId获取历史消息
     *
     * @param itemQueryParam
     * @return
     */
    PageResult<ChatMessage> selectHistory(ItemQueryParam itemQueryParam);

    /**
     * 添加收藏功能
     *
     * @param id
     * @return
     */
    int favourite(Long id);

    /**
     * 查看收藏功能
     *
     * @return
     */
    PageResult<GoodsVO> selectFavourite(ItemQueryParam itemQueryParam);

    /**
     * 得到账号基本信息
     *
     * @param account
     * @return
     */
    User selectAccountInfo(String account);

    /**
     * 修改密码
     *
     * @param password
     * @return
     */
    void updatePassword(@Param("password") String password);

    //取消收藏功能
    Integer favouriteRM(Long id);

    //发起会话
    Long createChatSession(Long toUserId,Long goodsId);

    //的到与当前用户对话的用户的id
    String getOtherId(Long sessionId, Long currentUserId);


    /**
     * 会话商品联想
     * 前端传递session_id后端根据session_id查看与它相关的商品简略信息并返回
     *
     */
    ProductAssociationVO tradeRequestLenovo(Long sessionId);

    //交易信息以及交易状态
    ProductAssociationVO selectTradeState(Long goodsId,Long session_id);

    //拒绝或者接受交易请求
    void HandleTradeRequest(Integer select, Long goods_id,Long session_id);

    //先看该用户是否有权利拒绝或者同意交易
    Integer getPermission(Long currentUserId, Long goodsId);

    //将某一商品的状态改为已经出售
    void saleGoods(Long goodsId,Long toUid,Long currentUid);

    //当前用户的待处理交易
    List<Pending> userPending(Long userId);

    //填写别人的验证码
    Integer putOtherVerifyCode(TradePairUp tradePairUp);

    //根据自己的id与商品id得到对面的id
    Long getOppositeId(Long myId, Long goodsId);
}
