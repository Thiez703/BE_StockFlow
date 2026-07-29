package com.vertex.stockflow.exception;

//dùng khi findById không tìm thấy (map ra HTTP 404 Not Found)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
