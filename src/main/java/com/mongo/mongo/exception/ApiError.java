package com.mongo.mongo.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
public class ApiError {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(FORMAT);

    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(String message, String reason, String status) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now().format(DATE_FORMAT);
    }
}
