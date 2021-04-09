package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackMessage {
    @JsonProperty("preds")
    private byte[] preds;
//    private List<Pixel> preds;

    @JsonProperty("token")
    private Token token;
}
