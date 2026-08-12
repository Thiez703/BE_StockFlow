package com.vertex.stockflow.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Integer id;
    private Integer conversationId;
    private String role;     // "USER" hoặc "ASSISTANT"
    private String content;  // Nội dung chat
    private LocalDateTime createdAt;
}
