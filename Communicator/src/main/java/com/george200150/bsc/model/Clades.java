package com.george200150.bsc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Clades {
    private List<String> clades;

    public Clades() {
        this.clades = new ArrayList<>();
    }

    public Clades(List<String> clades) {
        this.clades = clades;
    }
}
