package com.george200150.bsc.pleasefirebase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bitmap {
    @SerializedName("height")
    @Expose
    private int height;

    @SerializedName("width")
    @Expose
    private int width;


    @SerializedName("pixels")
    @Expose
    private byte[] pixels;
//    private List<Pixel> pixels;

    public void setPixels(byte[] pixels) {
        this.pixels = pixels;
    }

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

//    public List<Pixel> getPixels() {
//        return pixels;
//    }

//    public void setPixels(List<Pixel> pixels) {
//        this.pixels = pixels;
//    }
}

