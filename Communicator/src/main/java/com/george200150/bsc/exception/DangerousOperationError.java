package com.george200150.bsc.exception;

public class DangerousOperationError extends RuntimeException {

    private static final long serialVersionUID = 5L;

    public DangerousOperationError() {
    }

    public DangerousOperationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DangerousOperationError(String message, Throwable cause) {
        super(message, cause);
    }

    public DangerousOperationError(String message) {
        super(message);
    }

    public DangerousOperationError(Throwable cause) {
        super(cause);
    }
}
