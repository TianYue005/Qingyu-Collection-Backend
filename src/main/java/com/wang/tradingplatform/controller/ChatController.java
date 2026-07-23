package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.services.ChatService;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    private final SnowflakeIdUtil snowflakeIdUtil;


    /**
     * 构造函数注入
     *
     * @param chatService
     * @param messagingTemplate
     */
    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate, SnowflakeIdUtil snowflakeIdUtil) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.snowflakeIdUtil = snowflakeIdUtil;
    }


    /**
     * 发送私聊消息
     * 将接收到的消息保存并转发给订阅了"/queue/private" 的特定用户
     *
     * @param principal
     * @param message
     */
    @MessageMapping("/chat.privateMessage")  //是类似于@PostMapping("/add")那样的东西，拦截有关信息
    public void sendPrivateMessage(Principal principal, ChatMessage message) {
        if (principal != null) {
            message.setFromUid(Long.valueOf(principal.getName()));
        }//得到发送用户的Id
        chatService.saveMessage(message);//写入Redis与Mysql  还没有检查 TODO
        //发送私聊消息
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getToUid()),  // 参数 1：接收者的唯一身份标识 (User)
                "/queue/private",                    // 参数 2：目的地的后续路径 (Destination)
                message                              // 参数 3：消息体载荷 (Payload)
        );
    }

    /**
     * 发送群聊消息 @DestinationVariable是用来提取路径参数的类似于@PathVariable  @Payload加不加都一样
     *
     * @param roomId    群聊id
     * @param message   消息
     * @param principal 当前用户，类似于ThreadLocal那样.在StompWebSocketConfig中已经存储过信息了
     * @return message
     */
    //TODO 还没有检查代码
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room.{roomId}")
    public ChatMessage sendGroupMessage(@DestinationVariable String roomId, @Payload ChatMessage message, Principal principal) {
        if (principal != null) {
            message.setFromUid(Long.valueOf(principal.getName()));
        }
        message.setGroupId(Long.valueOf(roomId));
        if (message.getGroupId() == null || message.getGroupId() == 0) {
            //没有传递id说明是新创建群聊
            message.setGroupId(snowflakeIdUtil.nextId());
        }
        chatService.saveMessage(message);
        return message;//将消息传递给@SendTo指向的地址
    }
}
