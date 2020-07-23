package com.george200150.bsc.model;

public class Prediction {
    private String character;
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
