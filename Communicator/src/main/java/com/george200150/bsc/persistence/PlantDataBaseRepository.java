package com.george200150.bsc.persistence;

import com.george200150.bsc.model.Bounds;
import com.george200150.bsc.model.Plant;
import com.george200150.bsc.util.PlantRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlantDataBaseRepository {

    @Value("${spring.queries.SELECT_PAGED}")
    private String SELECT_PAGED;

    @Value("${spring.queries.SELECT_BY_LATIN_NAME}")
    private String SELECT_BY_LATIN_NAME;

    @Value("${spring.queries.SELECT_BY_ENGLISH_NAME}")
    private String SELECT_BY_ENGLISH_NAME;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Plant> getPagedRecords(Bounds interval) {
        log.debug("Entered class = PlantDataBaseRepository & method = getPagedRecords & Bounds interval = {}", interval);
        int from = interval.getOffset();
        int howMany = interval.getLimit();
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("from", from).addValue("offset", howMany);
        List<Plant> plants = namedParameterJdbcTemplate.query(SELECT_PAGED, namedParameters, new PlantRowMapper());
        log.debug("Exit class = PlantDataBaseRepository & method = getPagedRecords & return List<Plant> plants = {}", plants);
        return plants;
    }

    public Plant getRecordByLatinName(String latinName) {
        log.debug("Entered class = PlantDataBaseRepository & method = getRecordByLatinName & String latinName = {}", latinName);
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("latinName", latinName);
        List<Plant> plants = namedParameterJdbcTemplate.query(SELECT_BY_LATIN_NAME, namedParameters, new PlantRowMapper());
        Plant plant = null;
        if (plants.size() > 0){
            plant = plants.get(0);
        }
        log.debug("Exit class = PlantDataBaseRepository & method = getRecordByLatinName & return Plant plant = {}", plant);
        return plant;
    }

    public Plant getRecordByEnglishName(String englishName) {
        log.debug("Entered class = PlantDataBaseRepository & method = getRecordByEnglishName & String englishName = {}", englishName);
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("englishName", englishName);
        List<Plant> plants = namedParameterJdbcTemplate.query(SELECT_BY_ENGLISH_NAME, namedParameters, new PlantRowMapper());
        Plant plant = null;
        if (plants.size() > 0){
            plant = plants.get(0);
        }
        log.debug("Exit class = PlantDataBaseRepository & method = getRecordByEnglishName & return Plant plant = {}", plant);
        return plant;
    }
}