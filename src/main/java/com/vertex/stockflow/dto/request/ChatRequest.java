package com.vertex.stockflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    
    // Nếu là đoạn chat mới hoàn toàn, truyền null. 
    // Nếu chat tiếp trong đoạn chat cũ, truyền ID của Conversation vào đây.
    private Integer conversationId;

    @NotBlank(message = "Message content must not be blank")
    private String message;
}
