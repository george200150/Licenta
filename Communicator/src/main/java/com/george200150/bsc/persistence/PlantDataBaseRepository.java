package com.george200150.bsc.persistence;

import com.george200150.bsc.model.Bounds;
import com.george200150.bsc.model.Plant;
import com.george200150.bsc.util.PlantRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlantDataBaseRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Plant> getPagedRecords(Bounds interval) {
        String queryString = "SELECT * FROM plants LIMIT ";
        int from = interval.getOffset();
        int howMany = interval.getLimit();
        queryString += from + "," + howMany + ";";
        return namedParameterJdbcTemplate.query(queryString, new PlantRowMapper());
    }

    public Plant getRecordByLatinName(String latinName) {
        String queryString = "SELECT * FROM plants WHERE `latinName` LIKE :latinName";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("latinName", latinName);
        List<Plant> plants = namedParameterJdbcTemplate.query(queryString, namedParameters, new PlantRowMapper());
        Plant plant = null;
        if (plants.size() > 0){
            plant = plants.get(0);
        }
        return plant;
    }

    public Plant getRecordByEnglishName(String englishName) {
        String queryString = "SELECT * FROM plants WHERE `englishName` LIKE :englishName";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("englishName", englishName);
        List<Plant> plants = namedParameterJdbcTemplate.query(queryString, namedParameters, new PlantRowMapper());
        Plant plant = null;
        if (plants.size() > 0){
            plant = plants.get(0);
        }
        return plant;
    }
}

//STUB
//Plant plant = new Plant();
//        plant.setFamily("FAMILY");
//
//                Interval blooming = new Interval();
//                blooming.setStartMonth(4);
//                blooming.setEndMonth(5);
//                plant.setBloomingSeason(blooming);
//
//                plant.setEnglishName("ENGLISH NAME: " + englishName);
//                plant.setGenus("GENUS");
//
//                Interval greening = new Interval();
//                greening.setStartMonth(3);
//                greening.setEndMonth(7);
//                plant.setGreeningSeason(greening);
//
//                plant.setId(1);
//                plant.setKingdom("KINGDOM");
//                plant.setLatinName("LATIN NAME");
//
//                Location location = new Location();
//                location.setDecimalDegreesN(12.235);
//                location.setDecimalDegreesE(45.2452);
//                plant.setLocation(location);
//
//                plant.setOrder("ORDER");
//                plant.setSpecies("SPECIES");
//                return plant;
