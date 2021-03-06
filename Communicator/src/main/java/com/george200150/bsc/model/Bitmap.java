package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bitmap {
    @JsonProperty("height")
    private int height;

    @JsonProperty("width")
    private int width;

    @JsonProperty("pixels")
    private int[] pixels;
}
