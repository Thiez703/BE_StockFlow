package com.vertex.stockflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

//format lỗi thống nhất toàn hệ thống
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp; //thời điểm lỗi xảy ra.
    private int status; //mã HTTP status, ví dụ 400, 404, 500
    private String errorCode; //mã lỗi, ví dụ: USER_NOT_FOUND, INVALID_REQUEST
    private String error; //tên lỗi
    private String message; //mô tả lỗi
    private String path; //đường dẫn gây lỗi
    private Map<String, String> errors; //chi tiết lỗi cho từng trường
}
