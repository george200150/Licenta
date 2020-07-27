package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
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
}
