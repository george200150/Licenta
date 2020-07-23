package com.george200150.bsc.model;

import java.util.ArrayList;
import java.util.List;

public class Clades {
    private List<String> clades;

    public Clades() {
        this.clades = new ArrayList<>();
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

    @Override
    public String toString() {
        return "Clades{" +
                "clades=" + clades +
                '}';
    }
}
