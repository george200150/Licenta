package com.george200150.bsc.exception;

public class PushNotificationException extends RuntimeException {

    private static final long serialVersionUID = 5L;

    public PushNotificationException() {
    }

    public PushNotificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PushNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PushNotificationException(String message) {
        super(message);
    }

    public PushNotificationException(Throwable cause) {
        super(cause);
    }
}
