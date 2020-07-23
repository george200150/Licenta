package com.george200150.bsc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Interval {
    @JsonProperty("start")
    private int startMonth;

    @JsonProperty("end")
    private int endMonth;

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "startMonth=" + startMonth +
                ", endMonth=" + endMonth +
                '}';
    }
}
