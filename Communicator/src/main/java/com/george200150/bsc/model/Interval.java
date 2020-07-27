package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Interval {
    @JsonProperty("start")
    private int startMonth;

    @JsonProperty("end")
    private int endMonth;
}
