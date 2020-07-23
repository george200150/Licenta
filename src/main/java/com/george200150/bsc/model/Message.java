package com.george200150.bsc.model;

public class Message {
    private Bitmap bitmap;
    private Token token;

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

    @Override
    public String toString() {
        return "Message{" +
                "bitmap=" + bitmap +
                ", token='" + token + '\'' +
                '}';
    }
}
