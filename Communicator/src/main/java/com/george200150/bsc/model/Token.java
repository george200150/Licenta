package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Token {
    @JsonProperty("message")
    private String message;

    public Token() { }

    public Token(String s) {
        this.message = s;
    }
}
