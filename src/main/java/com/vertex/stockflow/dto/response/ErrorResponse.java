package com.vertex.stockflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * KHUÔN JSON LỖI thống nhất trả về cho FE, thay vì trang lỗi mặc định của Spring.
 * FE dựa vào các trường này để hiển thị thông báo:
 *   status    : mã HTTP (400, 404, 500...)
 *   message   : mô tả lỗi
 *   timestamp : thời điểm xảy ra
 *   errors    : chi tiết lỗi theo từng field (chỉ có khi lỗi validation), vd {"barcode":"..."}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
}