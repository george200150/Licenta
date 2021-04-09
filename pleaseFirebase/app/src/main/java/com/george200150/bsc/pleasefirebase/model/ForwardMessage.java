package com.george200150.bsc.pleasefirebase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForwardMessage {
    @SerializedName("bitmap")
    @Expose
    private Bitmap bitmap;

    @SerializedName("token")
    @Expose
    private Token token;

    @SerializedName("method") // TODO: hope I mapped it well...
    @Expose
    private Integer method;

    public ForwardMessage() {
    }

    public ForwardMessage(Bitmap bitmap, Token token, Integer method) {
        this.bitmap = bitmap;
        this.token = token;
        this.method = method;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ForwardMessage{" +
                "bitmap=" + bitmap +
                ", token=" + token +
                ", method=" + method +
                '}';
    }
}
