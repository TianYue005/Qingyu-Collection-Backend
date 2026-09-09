package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.ChatMessageListVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.ChatService;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.RandomCodeUtil;
import com.wang.tradingplatform.utils.RedisUtil;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import com.wang.tradingplatform.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/websocket")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final userService userService;
    private final RedisUtil redisUtil;


    /**
     * 构造函数注入
     *
     * @param chatService
     * @param messagingTemplate
     */
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate, userService userService, RedisUtil redisUtil) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.redisUtil = redisUtil;
    }


    /**
     * 发送私聊消息
     * 将接收到的消息保存并转发给订阅了"/queue/private" 的特定用户
     *
     * @param principal
     * @param chatMessage
     */
    @MessageMapping("/chat/privateMessage")  //是类似于@PostMapping("/add")那样的东西，拦截有关信息
    public void sendPrivateMessage(Principal principal, ChatMessage chatMessage) {
        // 0为正常聊天 1为更新为已读状态
        if (chatMessage.getType() == 0) {
            if (principal != null) {
                chatMessage.setFromUid(Long.valueOf(principal.getName()));
            }//得到发送用户的Id
            chatService.saveMessage(chatMessage);//写入Redis与Mysql  还没有检查 TODO
            //发送私聊消息
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(chatMessage.getToUid()),  // 参数 1：接收者的用户ID
                    "/queue/private",                        // 参数 2：目的地的后续路径
                    chatMessage                              // 参数 3：消息体载荷
            );
        } else if (chatMessage.getType() == 1) {
            System.out.println("TODO ");
        }
    }

    //用户会话列表
    @Operation(summary = "得到对应用户的会话列表")
    @GetMapping("/chatlist")
    public Result<List<ChatMessageListVO>> chatList() {
        log.info("========== 获取用户会话列表 ==========");
        return Result.success(userService.selectChatList());
    }

    //根据传递的sessionId获取历史消息
    @Operation(summary = "根据传递的sessionId获取历史消息")
    @PostMapping("/historyMessage")
    public Result<PageResult<ChatMessage>> history(@RequestBody ItemQueryParam itemQueryParam) {
        log.info("========== 获取历史消息 ==========");
        return Result.success(userService.selectHistory(itemQueryParam));
    }

    /**
     * 发起会话(私聊)  用户点击了发起会话的按钮
     *
     * @param toUserId 目标用户的用户id
     * @param goodsId  当前想要聊的商品 如果传递0说明不谈论商品，否则传递商品id
     * @return 无论如何都会返回一个sessionId（会话id）
     */
    @Operation(summary = "发起会话")
    @PostMapping("/chat/{toUserId}/{goodsId}")
    public Long createChatSession(@PathVariable Long toUserId, @PathVariable Long goodsId) {
        log.info("========== 发起会话 ==========");
        return userService.createChatSession(toUserId, goodsId);
    }


    /**
     * 会话商品联想
     * 前端传递session_id后端根据session_id查看与它相关的商品简略信息并返回
     *
     * @return 如果返回的是null则说明不需要联想 如果不是则加载数据
     */
    @Operation(summary = "会话商品联想")
    @GetMapping("/tradeRequest/lenovo/{sessionId}")
    public ProductAssociationVO tradeRequestLenovo(@PathVariable Long sessionId) {
        log.info("========== 会话商品联想 ==========");
        return userService.tradeRequestLenovo(sessionId);
    }

    /**
     * 交易信息以及交易状态
     * 前端传递商品id，返回商品简略信息以及交易状态
     * 0未处理 1已接受 2已拒绝
     */
    @Operation(summary = "交易信息以及交易状态")
    @GetMapping("/tradeRequest/tradeState/{session_id}/{goods_id}")
    public ProductAssociationVO selectTradeState(@PathVariable Long goods_id, @PathVariable Long session_id) {
        log.info("========== 交易信息以及交易状态 ==========");
        ProductAssociationVO vo = userService.selectTradeState(goods_id, session_id);
        System.out.println(vo);
        return vo;
    }

    //拒绝或者接受交易请求
    //交易状态 0 未确认 1 已有请求 2已同意请求 3已拒绝 4交易已完成
    @Operation(summary = "拒绝或者接受交易请求")
    @PostMapping("/tradeRequest/request")
    @Transactional(rollbackFor = Exception.class)
    public void HandleTradeRequest(@RequestBody TradeRequest tradeRequest) {
        //先看该用户是否有权利
        Integer row = userService.getPermission(UserContext.getCurrentUserId(), tradeRequest.getGoodsId());
        if (row > 0) {
            //将trade_transaction表进行数据同步
            userService.HandleTradeRequest(tradeRequest.getSelect(), tradeRequest.getGoodsId(), tradeRequest.getSessionId());
            if (tradeRequest.getTradeState() == 2) {
                //将goods表进行同步
                userService.saleGoods(tradeRequest.getGoodsId(), tradeRequest.getToUid(), UserContext.getCurrentUserId());
            }
        }
    }

    @Operation(summary = "得到自己的验证码")
    @PostMapping("/tradeRequest/myVerifyCode")
    public String getMyVerifyCode(@RequestBody TradePairUp tradePairUp) {
        //计算出合理的key
        Long myId = UserContext.getCurrentUserId();
        Long otherId = userService.getOppositeId(myId, tradePairUp.getGoodsId());
        String key = "MyVerifyCode" + myId + otherId;
        String randomCode = (String) redisUtil.get(key);
        if (randomCode == null) {
            //说明在redis内没有存储该用户的验证码
            randomCode = RandomCodeUtil.generate();
            redisUtil.set(key, randomCode, 5, TimeUnit.MINUTES);
        }
        return randomCode;
    }

    //填写别人的验证码  自己是第一个则返回1 自己是第二个则返回2 其他都为错误
    @Operation(summary = "填写别人的验证码")
    @PostMapping("/tradeRequest/otherVerifyCode")
    public Integer putOtherVerifyCode(@RequestBody TradePairUp tradePairUp) {
        log.info("========== 填写验证码 ==========");
        return userService.putOtherVerifyCode(tradePairUp);
    }

    //拒绝交易 todo
    @Operation(summary = "拒绝交易")
    @PostMapping("/tradeRequest/Reject")
    public Result<Object> RejectTradeRequest() {
        return null;
    }

    //交易结束，进行评价 TODO


}
