package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.vo.ChatMessageListVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.ChatService;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/websocket")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    private final SnowflakeIdUtil snowflakeIdUtil;

    private final userService userService;

    /**
     * 构造函数注入
     *
     * @param chatService
     * @param messagingTemplate
     */
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate, SnowflakeIdUtil snowflakeIdUtil, userService userService) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.snowflakeIdUtil = snowflakeIdUtil;
        this.userService = userService;
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
                    String.valueOf(chatMessage.getToUid()),  // 参数 1：接收者的唯一身份标识 (User)
                    "/queue/private",                        // 参数 2：目的地的后续路径 (Destination)
                    chatMessage                              // 参数 3：消息体载荷 (Payload)
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

    //获取历史消息
    @Operation(summary = "根据传递的sessionId获取历史消息")
    @PostMapping("/historyMessage")
    public Result<PageResult<ChatMessageListVO>> history(@RequestBody ItemQueryParam itemQueryParam) {
        log.info("========== 获取历史消息 ==========");
        return Result.success(userService.selectHistory(itemQueryParam));
    }

    /**
     * 发起会话(私聊)  用户点击了发起会话的按钮
     *
     * @param toUserId 目标用户的用户id
     * @return 无论如何都会返回一个sessionId（会话id）
     */
    @Operation(summary = "发起会话")
    @PostMapping("/chat/{toUserId}")
    public Long createChatSession(@PathVariable Long toUserId) {
        log.info("========== 发起会话 ==========");
        return userService.createChatSession(toUserId);
    }


}
