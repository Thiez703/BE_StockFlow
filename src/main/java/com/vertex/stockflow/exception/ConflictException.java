package com.vertex.stockflow.exception;

// Dùng khi thao tác bị từ chối vì xung đột với dữ liệu liên quan đang tồn tại
// (ví dụ: xóa Category trong khi vẫn còn danh mục con hoặc còn Product tham chiếu).
// GlobalExceptionHandler sẽ bắt exception này và trả về HTTP 409.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
