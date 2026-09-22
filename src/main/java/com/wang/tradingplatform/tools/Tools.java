package com.wang.tradingplatform.tools;

import com.wang.tradingplatform.exception.BusinessException;
import com.wang.tradingplatform.pojo.entity.riskAssessment;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.UserContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Tools {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private userService userService;

    @Tool(description = "向向Qdrant中存储数据 一次只能存储一个 如果想要存储多个请多次调用")
    public void saveData(@ToolParam(description = "存储的文本内容") String content,
                         @ToolParam(description = "元数据的键的值") String key,
                         @ToolParam(description = "元数据的值的值") String value) {
        Document doc = Document.builder()
                .text(content)
                .metadata(key, value)
                .build();
        vectorStore.add(List.of(doc));
    }

    @Tool(description = "看指定用户的交易数据以实现交易风险评估")
    public String riskAssessment(@ToolParam(description = "被查询的用户的id") Long userid) {
        return userService.riskAssessment(userid);
    }
}
