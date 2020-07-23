package com.george200150.bsc.model;

//import lombok.Data;

import java.util.List;

//@Data
public class BackMessage {
    private List<Prediction> preds;
    private Token token;

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
