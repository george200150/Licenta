package com.george200150.bsc.model;

public class Token {
    private String message;

    public Token(String s) {
        this.message = s;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
