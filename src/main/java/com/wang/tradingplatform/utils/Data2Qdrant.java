package com.wang.tradingplatform.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class Data2Qdrant {
    @Autowired
    private VectorStore vectorStore;

    @Tool(description = "向向量数据库中存储数据 一次只能存储一个 如果想要存储多个请多次调用")
    public void saveData(@ToolParam(description = "存储的文本内容") String content,
                         @ToolParam(description = "元数据的键的值") String key,
                         @ToolParam(description = "元数据的值的值") String value) {
        Document doc = Document.builder()
                .text(content)
                .metadata(key, value)
                .build();
        vectorStore.add(List.of(doc));
    }
}
