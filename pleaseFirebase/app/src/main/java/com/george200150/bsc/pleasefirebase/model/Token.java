package com.george200150.bsc.pleasefirebase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("message")
    @Expose
    private String message;

    public Token() {
    }

    public Token(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Token{" +
                "message='" + message + '\'' +
                '}';
    }
}
