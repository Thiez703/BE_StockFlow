package com.vertex.stockflow.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface WarehouseAssistant {

    String chat(@MemoryId Integer conversationId, @UserMessage String userMessage);
}
