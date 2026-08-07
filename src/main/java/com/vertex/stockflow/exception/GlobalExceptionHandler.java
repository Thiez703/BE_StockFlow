package com.vertex.stockflow.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import com.vertex.stockflow.dto.response.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

//Kiểm tra lỗi của controller
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<ErrorResponse> handleIllegalOperation(IllegalOperationException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "ILLEGAL_OPERATION", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Validation failed", request, errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", ex.getMessage(), request, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY", "Request body is invalid or missing", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", "Data integrity violation", request, null);
    }

    // Xung đột ghi đồng thời trên cột version (InventoryEntity/StocktakeEntity/AbnormalStockEntity) -
    // xảy ra khi 2 request cùng sửa 1 dòng gần như đồng thời (vd 2 người cùng duyệt 1 phiếu).
    // Bắt ConcurrencyFailureException (lớp cha) thay vì chỉ ObjectOptimisticLockingFailureException,
    // vì test thực tế 2 request duyệt song song cho thấy MySQL có thể trả về deadlock thật
    // (CannotAcquireLockException) thay vì optimistic-lock mismatch thuần túy - cả 2 đều là
    // ConcurrencyFailureException nên cùng 1 handler xử lý được cả 2 trường hợp.
    // Không xử lý riêng thì sẽ rơi vào handleGeneric() bên dưới -> trả 500 mập mờ thay vì 409 rõ nghĩa.
    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ErrorResponse> handleConcurrencyFailure(ConcurrencyFailureException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "CONCURRENT_MODIFICATION",
                "Dữ liệu vừa được cập nhật bởi người khác, vui lòng tải lại và thử lại", request, null);
    }

    //Lỗi ko lg trước(sập mySQL...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Lỗi tại {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Đã có lỗi xảy ra, vui lòng thử lại sau", request, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Email hoặc mật khẩu không đúng", request, null);
    }



    private ResponseEntity<ErrorResponse> build(HttpStatus status, String errorCode, String message, HttpServletRequest request, Map<String, String> errors) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .errorCode(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
