package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
    @JsonProperty("N")
    private Double decimalDegreesN;
    @JsonProperty("E")
    private Double decimalDegreesE;

    public Double getDecimalDegreesN() {
        return decimalDegreesN;
    }

    public void setDecimalDegreesN(Double decimalDegreesN) {
        this.decimalDegreesN = decimalDegreesN;
    }

    public Double getDecimalDegreesE() {
        return decimalDegreesE;
    }

    public void setDecimalDegreesE(Double decimalDegreesE) {
        this.decimalDegreesE = decimalDegreesE;
    }

    @Override
    public String toString() {
        return "Location{" +
                "decimalDegreesN=" + decimalDegreesN +
                ", decimalDegreesE=" + decimalDegreesE +
                '}';
    }
}
