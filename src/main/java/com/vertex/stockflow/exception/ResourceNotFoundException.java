package com.vertex.stockflow.exception;

// Dùng khi client truy vấn/thao tác tới một resource (theo id trên URL) không tồn tại trong DB.
// GlobalExceptionHandler sẽ bắt exception này và trả về HTTP 404.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
