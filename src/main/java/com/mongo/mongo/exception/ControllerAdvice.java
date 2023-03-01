package com.mongo.mongo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {


    @ExceptionHandler(BadQueryException.class)
    public ResponseEntity<ApiError> handleUserNotFound(BadQueryException e) {
        return new ResponseEntity<>(e.get(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(ObjectNotFoundException e) {
        return new ResponseEntity<>(e.get(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileNotUploadedException.class)
    public ResponseEntity<ApiError> handleUserNotFound(FileNotUploadedException e) {
        return new ResponseEntity<>(e.get(), HttpStatus.NOT_FOUND);
    }
}
