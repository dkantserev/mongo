package com.mongo.mongo.exception;

import org.springframework.http.HttpStatus;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ApiError get() {
        String reason = "object not found";
        return new ApiError(getMessage(), reason, HttpStatus.NOT_FOUND.toString());
    }
}
