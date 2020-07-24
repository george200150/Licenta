package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
    @JsonProperty("message")
    private String message;

    public Token() { }

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
