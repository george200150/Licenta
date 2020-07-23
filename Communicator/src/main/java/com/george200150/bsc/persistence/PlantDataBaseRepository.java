package com.george200150.bsc.persistence;

import com.george200150.bsc.model.Bounds;
import com.george200150.bsc.model.Interval;
import com.george200150.bsc.model.Location;
import com.george200150.bsc.model.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantDataBaseRepository {
    public List<Plant> getPagedRecords(Bounds interval) {
        return new ArrayList<>();
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
        plant.setLatinName("LATIN NAME");

        Location location = new Location();
        location.setDecimalDegreesN(12.235);
        location.setDecimalDegreesE(45.2452);
        plant.setLocation(location);

        plant.setOrder("ORDER");
        plant.setSpecies("SPECIES");
        return plant;
    }

    public Plant getRecordByEnglishName(String englishName) {
        return new Plant();
    }
}
