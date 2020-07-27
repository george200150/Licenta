package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Location {
    @JsonProperty("N")
    private Double decimalDegreesN;
    @JsonProperty("E")
    private Double decimalDegreesE;
}
