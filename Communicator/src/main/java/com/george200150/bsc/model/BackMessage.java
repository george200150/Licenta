package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackMessage {
    @JsonProperty("h")
    private int h;

    @JsonProperty("w")
    private int w;

    @JsonProperty("preds")
    private int[] preds;

    @JsonProperty("token")
    private Token token;
}
