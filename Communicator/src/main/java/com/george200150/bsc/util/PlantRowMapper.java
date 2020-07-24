package com.george200150.bsc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.model.Clades;
import com.george200150.bsc.model.Interval;
import com.george200150.bsc.model.Location;
import com.george200150.bsc.model.Plant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PlantRowMapper implements RowMapper<Plant> {

    @Autowired
    private ObjectMapper objectMapper;

    public PlantRowMapper() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Plant mapRow(ResultSet rs, int rowNum) {
        Plant plant = new Plant();
        this.setPlantData(plant, rs);
        return plant;
    }

    private void setPlantData(Plant plant, ResultSet rs) {
        try {
            plant.setId(rs.getInt("id"));
            plant.setEnglishName(rs.getString("englishName"));
            plant.setLatinName(rs.getString("latinName"));
            plant.setKingdom(rs.getString("kingdom"));
            plant.setClades(objectMapper.readValue(rs.getString("clades"), Clades.class));
            plant.setDivision(rs.getString("division"));
            plant.setPlantClass(rs.getString("plantClass"));
            plant.setOrder(rs.getString("order"));
            plant.setFamily(rs.getString("family"));
            plant.setSubFamily(rs.getString("subFamily"));
            plant.setGenus(rs.getString("genus"));
            plant.setSpecies(rs.getString("species"));
            plant.setLocation(objectMapper.readValue(rs.getString("location"), Location.class));
            plant.setGreeningSeason(objectMapper.readValue(rs.getString("greeningSeason"), Interval.class));
            plant.setBloomingSeason(objectMapper.readValue(rs.getString("bloomingSeason"), Interval.class));
        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            // TODO: throw custom exception, I guess...
        }
    }
}
