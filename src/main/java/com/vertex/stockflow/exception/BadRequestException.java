package com.vertex.stockflow.exception;

// Dùng khi dữ liệu client gửi lên (request body) không hợp lệ về mặt nghiệp vụ
// (ví dụ tham chiếu tới một id không tồn tại, hoặc tạo ra trạng thái dữ liệu vô lý như vòng lặp cha-con).
// GlobalExceptionHandler sẽ bắt exception này và trả về HTTP 400.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
