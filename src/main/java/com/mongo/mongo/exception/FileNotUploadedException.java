package com.mongo.mongo.exception;

import org.springframework.http.HttpStatus;



public class FileNotUploadedException extends RuntimeException {
    public FileNotUploadedException(String message) {
        super(message);
    }

    public ApiError get() {
        String reason = "file not uploaded. download the file at url $mongo/upload";
        return new ApiError(getMessage(), reason, HttpStatus.CONFLICT.toString());
    }
}
