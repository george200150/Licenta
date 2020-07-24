package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Prediction {
    @JsonProperty("character")
    private String character;

    @JsonProperty("percentage")
    private int percentage;

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "character='" + character + '\'' +
                ", percentage=" + percentage +
                '}';
    }
}
