package com.george200150.bsc.persistence;

import com.george200150.bsc.model.Bounds;
import com.george200150.bsc.model.Interval;
import com.george200150.bsc.model.Location;
import com.george200150.bsc.model.Plant;
import com.george200150.bsc.util.PlantRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO: Fri Jul 24 02:33:42 EEST 2020 WARN: Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.


@Component
public class PlantDataBaseRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

//    @Autowired
//    public PlantDataBaseRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate){
//        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
//    }

    public List<Plant> getPagedRecords(Bounds interval) {
        return new ArrayList<>();
    }

    public List<Plant> getAll(){
        String queryString = "SELECT * FROM plants;";
        return namedParameterJdbcTemplate.query(queryString, new PlantRowMapper());
    }

    public Plant getRecordByLatinName(String latinName) {
        Plant plant = new Plant();
        plant.setFamily("FAMILY");

        Interval blooming = new Interval();
        blooming.setStartMonth(4);
        blooming.setEndMonth(5);
        plant.setBloomingSeason(blooming);

        plant.setEnglishName("ENGLISH NAME");
        plant.setGenus("GENUS");

        Interval greening = new Interval();
        greening.setStartMonth(3);
        greening.setEndMonth(7);
        plant.setGreeningSeason(greening);

        plant.setId(1);
        plant.setKingdom("KINGDOM");
        plant.setLatinName("LATIN NAME: " + latinName);

        Location location = new Location();
        location.setDecimalDegreesN(12.235);
        location.setDecimalDegreesE(45.2452);
        plant.setLocation(location);

        plant.setOrder("ORDER");
        plant.setSpecies("SPECIES");
        return plant;
    }

    public Plant getRecordByEnglishName(String englishName) {
        Plant plant = new Plant();
        plant.setFamily("FAMILY");

        Interval blooming = new Interval();
        blooming.setStartMonth(4);
        blooming.setEndMonth(5);
        plant.setBloomingSeason(blooming);

        plant.setEnglishName("ENGLISH NAME: " + englishName);
        plant.setGenus("GENUS");

        Interval greening = new Interval();
        greening.setStartMonth(3);
        greening.setEndMonth(7);
        plant.setGreeningSeason(greening);

        plant.setId(1);
        plant.setKingdom("KINGDOM");
        plant.setLatinName("LATIN NAME");

        Location location = new Location();
        location.setDecimalDegreesN(12.235);
        location.setDecimalDegreesE(45.2452);
        plant.setLocation(location);

        plant.setOrder("ORDER");
        plant.setSpecies("SPECIES");
        return plant;
    }
}
