package com.george200150.bsc.pleasefirebase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pixel {
    @SerializedName("R")
    @Expose
    private int R;

    @SerializedName("G")
    @Expose
    private int G;

    @SerializedName("B")
    @Expose
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
}
