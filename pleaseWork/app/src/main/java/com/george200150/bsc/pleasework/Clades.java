package com.george200150.bsc.pleasework;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Clades {
    @SerializedName("clades")
    @Expose
    private List<String> clades;

    public Clades() {
    }

    public Clades(List<String> clades) {
        this.clades = clades;
    }

    public List<String> getClades() {
        return clades;
    }

    public void setClades(List<String> clades) {
        this.clades = clades;
    }
}
