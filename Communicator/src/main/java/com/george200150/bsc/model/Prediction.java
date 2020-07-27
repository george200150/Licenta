package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Prediction {
    @JsonProperty("character")
    private String character;

    @JsonProperty("percentage")
    private int percentage;

    public Prediction() {
    }

    public Prediction(String character, int percentage) {
        this.character = character;
        this.percentage = percentage;
    }
}
