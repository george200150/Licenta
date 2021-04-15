package com.george200150.bsc.exception;

public class QueueProxyException extends RuntimeException {

    private static final long serialVersionUID = 6L;

    public QueueProxyException() {
    }

    public QueueProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public QueueProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueueProxyException(String message) {
        super(message);
    }

    public QueueProxyException(Throwable cause) {
        super(cause);
    }
}
