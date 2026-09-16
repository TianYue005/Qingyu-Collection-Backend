package com.wang.tradingplatform.utils;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI 客服的"对话记忆"存储实现（双存储：Redis + MySQL，目前为空壳骨架，尚未真正实现）。
 * <p>
 * 它实现了 Spring AI 的 {@link ChatMemoryRepository} 接口，
 * 框架在需要读写聊天历史时会自动调用下面这四个方法。
 * 目前所有方法都是空实现，所以 AI 客服暂时"记不住"历史对话。
 */
@Component
public class DualStorageChatMemoryRepository implements ChatMemoryRepository {

    /**
     * 查出系统里所有对话（会话）的 ID 列表。
     * <p>
     * 作用：例如 AI 客服和 10 个用户聊过天，这里应返回那 10 个会话的 ID。
     *
     * @return 所有会话 ID 的字符串列表；当前返回空列表，表示"没有任何会话"
     */
    @Override
    public @NonNull List<String> findConversationIds() {
        return List.of();
    }

    /**
     * 根据某个会话 ID，查出该会话已经聊过的所有历史消息。
     * <p>
     * 作用：AI 客服在回复前，会靠这个方法把之前的上下文"回忆"起来。
     *
     * @param conversationId 会话的唯一标识（字符串）
     * @return 该会话的历史消息列表（{@link Message} 表示一条消息，含角色、内容等）；当前返回空列表，表示"查不到历史消息"
     */
    @Override
    public @NonNull List<Message> findByConversationId(@NonNull String conversationId) {
        return List.of();
    }

    /**
     * 保存某个会话的一批消息。
     * <p>
     * 作用：每轮对话结束后，框架会把这次的新消息（用户问题 + AI 回答）通过本方法存进记忆库，供下次查询。
     *
     * @param conversationId 会话的唯一标识（字符串）
     * @param messages       要保存的消息列表
     */
    @Override
    public void saveAll(@NonNull String conversationId, @NonNull List<Message> messages) {

    }

    /**
     * 删除某个会话的所有聊天记录。
     * <p>
     * 作用：例如用户要求清除历史、或会话结束需要清理时调用。
     *
     * @param conversationId 要删除的会话的唯一标识（字符串）
     */
    @Override
    public void deleteByConversationId(@NonNull String conversationId) {

    }
}