package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.utils.Data2Qdrant;
import com.wang.tradingplatform.utils.ParamUtil;
import com.wang.tradingplatform.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/customer/service")
public class AiCustomerServiceController {

    private final ChatClient chatClient;


    /**
     * 注入 RAG ChatClient 并挂载对话记忆。
     * <p>
     * 注意：这里同样【不注册任何工具】，AI 客服只有"查看（RAG 检索）+ 文字回复 + 记忆读写"能力，
     * 无法执行下单、修改、删除等写操作。
     */
    public AiCustomerServiceController(@Qualifier("ragChatClient") ChatClient ragChatClient, ChatMemory chatMemory) {
        this.chatClient = ragChatClient.mutate()
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    //Ai客服
    @Operation(summary = "Ai客服")
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String message) {
        ParamUtil.notBlank(message, "消息内容");
        Long userId = UserContext.getCurrentUserId();          // 从 JWT 拿到当前用户
        String conversationId = "ai-service-" + userId;        // 每个用户一个独立会话
        return chatClient.prompt()
                .user(message)
                .system(
                        "你是校园二手交易平台「轻寓集」的 AI 客服，负责解答平台使用、交易流程、账号等问题，语气友好简洁，被问道不知道的问题就直接回答不知道。" +
                                "被问到关于政治，敏感话题等可能出现错误的问题都要拒绝回答。拒绝回复角色扮演与回答能力范围外的问题")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()      // 触发流式调用
                .content();    // 提取返回的文本内容
    }
}
