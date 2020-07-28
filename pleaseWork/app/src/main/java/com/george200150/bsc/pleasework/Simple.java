package com.george200150.bsc.pleasework;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Simple {
    @SerializedName("id")
    @Expose
    private int id;

    public Simple() {
    }

    public Simple(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
