package com.george200150.bsc.exception;

public class CustomRabbitException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomRabbitException() {
    }

    public CustomRabbitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CustomRabbitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomRabbitException(String message) {
        super(message);
    }

    public CustomRabbitException(Throwable cause) {
        super(cause);
    }
}
