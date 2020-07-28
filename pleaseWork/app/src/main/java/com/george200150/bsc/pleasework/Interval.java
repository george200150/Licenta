package com.george200150.bsc.pleasework;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Interval {
    @SerializedName("start")
    @Expose
    private int startMonth;

    @SerializedName("end")
    @Expose
    private int endMonth;

    public Interval() {
    }

    public Interval(int startMonth, int endMonth) {
        this.startMonth = startMonth;
        this.endMonth = endMonth;
    }

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
}
