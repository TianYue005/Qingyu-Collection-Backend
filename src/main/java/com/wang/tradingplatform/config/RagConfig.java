package com.wang.tradingplatform.config;


import com.wang.tradingplatform.utils.Data2Qdrant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    /**
     * 构建 AI 客服的 ChatClient（RAG 检索 + 文字回复）。
     * <p>
     * 重要约束：这里【故意不注册任何工具】。
     * Spring AI 中，模型只能调用通过 {@code .tools()} / {@code .functions()} 显式注册的工具；
     * 只要不注册，AI 就没有任何执行代码 / 写操作（下单、改资料、删数据）的能力，
     * 只能做 RAG 知识库检索（纯查看）和文字回答。
     * 未来若要扩展能力，只允许注册"只读查询"类工具（SELECT），禁止注册增删改类工具。
     */
    @Bean
    public ChatClient ragChatClient(@Qualifier("deepSeekChatModel") ChatModel chatModel, VectorStore vectorStore, Data2Qdrant data2Qdrant) {
        // 构建 RAG 顾问，设置相似度阈值和返回文档数量
        var advisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.7) // 相似度阈值
                        .topK(4)                  // 检索文档数量
                        .build())
                .build();
        /*var toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(data2Qdrant)   // 把 @Tool 方法注册进去
                .build();*/
        return ChatClient.builder(chatModel)
                .defaultAdvisors(advisor)
                //.defaultTools(toolProvider)
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory
                .builder()
                .maxMessages(6)//滑动窗口大小
                .build();
    }
}