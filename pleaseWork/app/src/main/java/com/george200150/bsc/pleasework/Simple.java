package com.george200150.bsc.pleasework;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Simple {
    @SerializedName("id")
    @Expose
    private String id;

    public Simple() {
    }

    public Simple(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Simple{" +
                "id='" + id + '\'' +
                '}';
    }
}
