package com.george200150.bsc.model;

public class Bounds {
    private int offset;
    private int limit;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "Bounds{" +
                "offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}

