package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Pixel {
    @JsonProperty("R")
    private int R;

    @JsonProperty("G")
    private int G;

    @JsonProperty("B")
    private int B;
}
