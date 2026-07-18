package com.vertex.stockflow.exception;

/**
 * Ném ra khi VI PHẠM QUY TẮC NGHIỆP VỤ (vd: barcode trùng, minStock > maxStock).
 * GlobalExceptionHandler bắt lỗi này và trả HTTP 400 cho FE.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}