package com.vertex.stockflow.exception;

import lombok.Getter;

@Getter
public class DuplicateUsernameException extends RuntimeException {
    private final String username;
    public DuplicateUsernameException(String username) {
        super("email đã tồn tại: " +username);
        this.username = username;
    }

}
