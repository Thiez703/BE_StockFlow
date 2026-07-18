package com.vertex.stockflow.exception;

/**
 * Ném ra khi KHÔNG TÌM THẤY dữ liệu (vd: id không tồn tại).
 * GlobalExceptionHandler bắt lỗi này và trả HTTP 404 cho FE.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}