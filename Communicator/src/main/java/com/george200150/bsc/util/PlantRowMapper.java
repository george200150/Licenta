//package com.george200150.bsc.util;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.george200150.bsc.exception.PlantMappingException;
//import com.george200150.bsc.model.Clades;
//import com.george200150.bsc.model.Interval;
//import com.george200150.bsc.model.Location;
//import com.george200150.bsc.model.Plant;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Component;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@Slf4j
//@Component
//public class PlantRowMapper implements RowMapper<Plant> {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    public PlantRowMapper() { // TODO: to be deleted
//        this.objectMapper = new ObjectMapper();
//    }
//
//    @Override
//    public Plant mapRow(ResultSet rs, int rowNum) {
//        log.debug("Entered class = PlantRowMapper & method = mapRow & ResultSet rs = {}, int rowNum = {}", rs, rowNum);
//        Plant plant = new Plant();
//        this.setPlantData(plant, rs);
//        log.debug("Exit class = PlantRowMapper & method = mapRow & return Plant plant = {}", plant);
//        return plant;
//    }
//
//    private void setPlantData(Plant plant, ResultSet rs) {
//        log.debug("Entered class = PlantRowMapper & method = setPlantData & Plant plant = {}, ResultSet rs = {}", plant, rs);
//        try {
//            log.debug("Entered try in setPlantData");
//            plant.setId(rs.getInt("id"));
//            plant.setEnglishName(rs.getString("englishName"));
//            plant.setLatinName(rs.getString("latinName"));
//            plant.setKingdom(rs.getString("kingdom"));
//            plant.setClades(objectMapper.readValue(rs.getString("clades"), Clades.class));
//            plant.setDivision(rs.getString("division"));
//            plant.setPlantClass(rs.getString("plantClass"));
//            plant.setOrder(rs.getString("order"));
//            plant.setFamily(rs.getString("family"));
//            plant.setSubFamily(rs.getString("subFamily"));
//            plant.setGenus(rs.getString("genus"));
//            plant.setSpecies(rs.getString("species"));
//            plant.setLocation(objectMapper.readValue(rs.getString("location"), Location.class));
//            plant.setGreeningSeason(objectMapper.readValue(rs.getString("greeningSeason"), Interval.class));
//            plant.setBloomingSeason(objectMapper.readValue(rs.getString("bloomingSeason"), Interval.class));
//            log.debug("Exit try in setPlantData");
//        } catch (JsonProcessingException | SQLException e) {
//            log.debug("Throw in setPlantData & final Exception e = {}", e);
//            // TODO: should I ignore the failed mapping or should I throw exception?
//            throw new PlantMappingException(e);
//        }
//        log.debug("Exit class = PlantRowMapper & method = setPlantData & return = void");
//    }
//}
