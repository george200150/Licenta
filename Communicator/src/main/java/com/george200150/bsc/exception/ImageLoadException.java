package com.george200150.bsc.exception;

public class ImageLoadException extends RuntimeException {

    private static final long serialVersionUID = 3L;

    public ImageLoadException() {
    }

    public ImageLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ImageLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageLoadException(String message) {
        super(message);
    }

    public ImageLoadException(Throwable cause) {
        super(cause);
    }
}
