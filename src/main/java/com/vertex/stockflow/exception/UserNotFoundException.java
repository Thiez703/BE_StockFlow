package com.vertex.stockflow.exception;

public class UserNotFoundException extends RuntimeException{
    private final Integer userId;

    public UserNotFoundException(Integer userId) {
        super("Không tìm thấy user với id: " + userId);
        this.userId = userId;
    }
}
