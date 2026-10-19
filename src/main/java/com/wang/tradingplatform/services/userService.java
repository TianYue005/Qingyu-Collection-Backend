package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.*;
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
     * @param loginDTO 登录请求参数
     * @return 登录结果（成功时返回 token）
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
     * @return 会话列表
     */
    List<ChatMessageListVO> selectChatList();

    /**
     * 根据传递的sessionId获取历史消息
     *
     * @param itemQueryParam 查询参数（含 sessionId、pageNumber、pageSize）
     * @return 历史消息分页结果
     */
    PageResult<ChatMessage> selectHistory(ItemQueryParam itemQueryParam);

    /**
     * 添加收藏功能
     *
     * @param id 商品id
     * @return 影响行数
     */
    int favourite(Long id);

    /**
     * 查看收藏功能
     *
     * @param itemQueryParam 分页参数
     * @return 收藏商品分页结果
     */
    PageResult<GoodsVO> selectFavourite(ItemQueryParam itemQueryParam);

    /**
     * 得到账号基本信息
     *
     * @param account 账号
     * @return 用户信息
     */
    User selectAccountInfo(String account);

    /**
     * 修改密码
     *
     * @param password 新密码
     */
    void updatePassword(@Param("password") String password);

    //取消收藏功能
    Integer favouriteRM(Long id);

    //发起会话
    Long createChatSession(Long toUserId, Long goodsId);

    //的到与当前用户对话的用户的id
    String getOtherId(Long sessionId, Long currentUserId);


    /**
     * 会话商品联想
     * 前端传递session_id后端根据session_id查看与它相关的商品简略信息并返回
     *
     */
    ProductAssociationVO tradeRequestLenovo(Long sessionId);

    //交易信息以及交易状态
    ProductAssociationVO selectTradeState(Long goodsId, Long session_id);

    /**
     * 处理交易请求（同意/拒绝）
     * 在同一事务中：校验当前用户权限 → 更新交易状态 → 同意时同步商品出售状态
     *
     * @param select    处理选项（2 表示同意）
     * @param goodsId   商品id
     * @param sessionId 会话id
     * @param toUid     交易对方id
     * @return 是否有权限处理该交易（无权限返回 false，不做任何改动）
     */
    boolean handleTradeRequest(Integer select, Long goodsId, Long sessionId, Long toUid);

    //当前用户的待处理交易
    List<Pending> userPending(Long userId);

    //填写别人的验证码
    Integer putOtherVerifyCode(TradePairUp tradePairUp);

    //根据自己的id与商品id得到对面的id
    Long getOppositeId(Long myId, Long goodsId);

    //查看评价：target=0 自己评价别人，其他为别人评价自己
    PageResult<EvaluateVO> selectEvaluate(ItemQueryParam itemQueryParam);

    //交易风险评估
    String riskAssessment(Long currentUserId, Long userid);

    //别的用户的详情界面
    User selectThisUserDetail(Long userId);

    //查看用户相关的评价 这个评论是指在交易完成之后 双方的互相评价
    PageResult<EvaluateVO> selectReview(ItemQueryParam itemQueryParam);

    //更改名字
    Boolean alterName(String name);
}
