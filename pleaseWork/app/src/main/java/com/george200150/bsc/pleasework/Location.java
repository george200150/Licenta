package com.george200150.bsc.pleasework;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("N")
    @Expose
    private Double decimalDegreesN;

    @SerializedName("E")
    @Expose
    private Double decimalDegreesE;

    public Location() {
    }

    public Location(Double decimalDegreesN, Double decimalDegreesE) {
        this.decimalDegreesN = decimalDegreesN;
        this.decimalDegreesE = decimalDegreesE;
    }

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
}
