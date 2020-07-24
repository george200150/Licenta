package com.george200150.bsc.controller;

import com.george200150.bsc.model.*;
import com.george200150.bsc.persistence.PlantDataBaseRepository;
import com.george200150.bsc.service.QueueProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("data")
@RestController
public class RestfulAndroidJavaController {

    @Autowired
    private PlantDataBaseRepository repository;

    @Autowired
    private QueueProxy server;

    @PostMapping("bitmap")
    @ResponseStatus(HttpStatus.OK)
    public Token handlePostCar(@RequestBody Bitmap bitmap) {
        // send bitmap for processing on Python server
        return server.send(bitmap);
    }

//    {
//        "height": 2,
//            "width": 2,
//            "pixels": [
//        {"R":0,
//                "G": 0,
//                "B": 0},
//        {"R":0,
//                "G": 0,
//                "B": 0},
//        {"R":0,
//                "G": 0,
//                "B": 0},
//        {"R":0,
//                "G": 0,
//                "B": 0}
//]
//    }

    @GetMapping("records/EN/{englishName}")
    @ResponseStatus(HttpStatus.OK)
    public Plant handleGetRecordbyEnglishName(@PathVariable String englishName) {
        return repository.getRecordByEnglishName(englishName);
    }

    @GetMapping("records/LAT/{latinName}")
    @ResponseStatus(HttpStatus.OK)
    public Plant handleGetRecordbyLatinName(@PathVariable String latinName) {
        return repository.getRecordByLatinName(latinName);
    }

    @PostMapping("records")
    @ResponseStatus(HttpStatus.OK)
    public List<Plant> handleGetRecords(@RequestBody Bounds interval) {
        return repository.getPagedRecords(interval);
    }
}
