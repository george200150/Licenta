package com.george200150.bsc.controller;

import com.george200150.bsc.exception.QueueProxyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {
    @ExceptionHandler(QueueProxyException.class)
    public ResponseEntity<String> sendQueueFailed(QueueProxyException exception) {
        log.warn("RabbitMQ queue call has failed: {}", exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
