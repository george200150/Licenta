package com.george200150.bsc.model;

//import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@Data
public class BackMessage {
    @JsonProperty("preds")
    private List<Prediction> preds;
    @JsonProperty("token")
    private Token token;

    public BackMessage() {
    }

    public BackMessage(List<Prediction> preds, Token token) {
        this.preds = preds;
        this.token = token;
    }

    public List<Prediction> getPreds() {
        return preds;
    }

    public void setPreds(List<Prediction> preds) {
        this.preds = preds;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "BackMessage{" +
                "preds=" + preds +
                ", token='" + token + '\'' +
                '}';
    }
}
