package com.george200150.bsc.util;

import com.george200150.bsc.model.Prediction;

import java.util.List;

public class ParseBuilder {

    // TODO: @Value -> get from a *.properties file the minimum threshold
    private static int threshold = 80;

    public static String parse(List<Prediction> probMap) {
        StringBuilder sb = new StringBuilder();
        probMap.stream().map(x -> {if(x.getPercentage() > 80) return x.getCharacter(); else return "."; }).forEach(sb::append);
        return sb.toString();
    }
}
