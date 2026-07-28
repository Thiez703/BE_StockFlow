package com.vertex.stockflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;
//format lỗi thống nhất toàn hệ thống
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
