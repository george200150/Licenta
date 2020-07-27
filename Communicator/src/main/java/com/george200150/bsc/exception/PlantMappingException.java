package com.george200150.bsc.exception;


public class PlantMappingException extends RuntimeException {

    private static final long serialVersionUID = 2L;

    public PlantMappingException() {
    }

    public PlantMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PlantMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlantMappingException(String message) {
        super(message);
    }

    public PlantMappingException(Throwable cause) {
        super(cause);
    }
}
