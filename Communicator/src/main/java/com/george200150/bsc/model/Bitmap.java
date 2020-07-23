package com.george200150.bsc.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bitmap {
    @JsonProperty("height")
    private int height;

    @JsonProperty("width")
    private int width;

    @JsonProperty("pixels")
    private List<Pixel> pixels;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<Pixel> getPixels() {
        return pixels;
    }

    public void setPixels(List<Pixel> pixels) {
        this.pixels = pixels;
    }

    @Override
    public String toString() {
        return "Bitmap{" +
                "height=" + height +
                ", width=" + width +
                ", pixels=" + pixels +
                '}';
    }
}

