package com.george200150.bsc.pleasefirebase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Method {
    @SerializedName("method")
    @Expose
    private int method;

    public Method(int method) {
        this.method = method;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Method{" +
                "method=" + method +
                '}';
    }
}
