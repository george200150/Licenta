package com.george200150.bsc.model;

import lombok.Data;

@Data
public class WrapperForwardMessage {
    private StringifiedBitmap bitmap;
    private Token token;
    private Integer method;

    public WrapperForwardMessage(ForwardMessage forwardMessage, String encodedBytes) {
        this.bitmap = new StringifiedBitmap(forwardMessage.getBitmap().getHeight(), forwardMessage.getBitmap().getWidth(), encodedBytes);
        this.token = forwardMessage.getToken();
        this.method = forwardMessage.getMethod();
    }
}
