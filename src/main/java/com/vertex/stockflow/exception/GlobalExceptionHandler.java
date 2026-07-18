package com.vertex.stockflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Unauthorized");
        response.put("message", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    // Bạn có thể thêm các @ExceptionHandler khác ở đây (ví dụ: MethodArgumentNotValidException cho Validation)
}
