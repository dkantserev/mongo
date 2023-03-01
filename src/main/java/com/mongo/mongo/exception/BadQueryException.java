package com.mongo.mongo.exception;

import org.springframework.http.HttpStatus;

public class BadQueryException extends RuntimeException{
    public BadQueryException(String message) {
        super(message);
    }

    public ApiError get() {
        String reason = "missing search options";
        return new ApiError(getMessage(), reason, HttpStatus.BAD_REQUEST.toString());
    }
}
