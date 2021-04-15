package com.george200150.bsc.model;

import lombok.Data;

@Data
public class ForwardMessage {
    private Bitmap bitmap;
    private Token token;
    private Method method;
}
