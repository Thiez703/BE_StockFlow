package com.vertex.stockflow.exception;

//dùng khi code bị trùng (map ra HTTP 409 Conflict)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
