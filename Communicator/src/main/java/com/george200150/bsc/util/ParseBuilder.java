package com.george200150.bsc.util;

import com.george200150.bsc.model.Prediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Slf4j
public class ParseBuilder {

    @Value("${spring.prediction.parser.threshold")
    private static int threshold = 80;

    public static String parse(List<Prediction> probMap) {
        log.debug("Entered class = ParseBuilder & method = parse & List<Prediction> probMap = {}", probMap);
        StringBuilder sb = new StringBuilder();
        probMap.stream().map(x -> {if(x.getPercentage() > 80) return x.getCharacter(); else return "."; }).forEach(sb::append);
        String plantText = sb.toString();
        log.debug("Exiting class = QueueProxy & method = send & return String plantText = {}", plantText);
        return plantText;
    }
}
