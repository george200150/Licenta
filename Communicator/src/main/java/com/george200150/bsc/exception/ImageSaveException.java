package com.george200150.bsc.exception;

public class ImageSaveException extends RuntimeException {

    private static final long serialVersionUID = 4L;

    public ImageSaveException() {
    }

    public ImageSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ImageSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageSaveException(String message) {
        super(message);
    }

    public ImageSaveException(Throwable cause) {
        super(cause);
    }
}
