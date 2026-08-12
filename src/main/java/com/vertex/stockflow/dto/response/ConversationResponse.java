package com.vertex.stockflow.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {
    private Integer id;
    private String title;
    private LocalDateTime createdAt;
}
