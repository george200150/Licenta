package com.george200150.bsc.controller;

import com.george200150.bsc.listener.MQManager;
import com.george200150.bsc.model.*;
import com.george200150.bsc.persistence.PlantDataBaseRepository;
import com.george200150.bsc.service.QueueProxy;
import com.george200150.bsc.util.ParseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Plant> handlePostCar(@RequestBody Bitmap bitmap) {
        // send bitmap for processing on Python server
        List<Prediction> probMap = server.process(bitmap);

        System.out.println("IN RESTFUL AFTER SERVER PROCESS:    " + probMap.toString());

        // transform the received data into text
        String latinName = ParseBuilder.parse(probMap);

        System.out.println("LATIN NAME:    " + latinName);

        // query the DB for a record
        Plant record = repository.getRecordByLatinName(latinName);

        // return the DB data to the client
        return new ResponseEntity<Plant>(record, HttpStatus.OK);
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

    //TODO: return object is not null, but it is an object with default values (0 or null)
    @GetMapping("records/EN/{englishName}")
    public ResponseEntity<Plant> handleGetRecordbyEnglishName(@PathVariable String englishName) {
        // query the DB for a record
        Plant record = repository.getRecordByEnglishName(englishName);
        // return the DB data to the client
        return new ResponseEntity<Plant>(record, HttpStatus.OK);
    }

    @GetMapping("records/LAT/{latinName}")
    public ResponseEntity<Plant> handleGetRecordbyLatinName(@PathVariable String latinName) {
        // query the DB for a record
        Plant record = repository.getRecordByLatinName(latinName);
        // return the DB data to the client
        return new ResponseEntity<Plant>(record, HttpStatus.OK);
    }

    @PostMapping("records")
    public ResponseEntity<List<Plant>> handleGetRecords(@RequestBody Bounds interval) {
        // query the DB for a record
        List<Plant> record = repository.getPagedRecords(interval);
        // return the DB data to the client
        return new ResponseEntity<List<Plant>>(record, HttpStatus.OK);
    }
}
