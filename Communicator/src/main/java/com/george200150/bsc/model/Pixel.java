package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pixel {
    @JsonProperty("R")
    private int R;

    @JsonProperty("G")
    private int G;

    @JsonProperty("B")
    private int B;

    public int getR() {
        return R;
    }

    public void setR(int r) {
        R = r;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "R=" + R +
                ", G=" + G +
                ", B=" + B +
                '}';
    }
}
