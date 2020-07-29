package com.george200150.bsc.controller;

import com.george200150.bsc.model.*;
import com.george200150.bsc.persistence.PlantDataBaseRepository;
import com.george200150.bsc.service.QueueProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("data")
@RestController
public class RestfulAndroidJavaController {

    @Autowired
    private PlantDataBaseRepository repository;

    @Autowired
    private QueueProxy server;



    @PostMapping(value = "simple",  consumes = "application/json;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public Simple handlePostSimple(@RequestBody Simple simple) {
        System.out.println(simple);
        log.debug("Entered class = RestfulAndroidJavaController & method = handlePostSimple & Simple simple = {}", simple);
        return simple;
    }


    @PostMapping("bitmap")
    @ResponseStatus(HttpStatus.OK)
    public Token handlePostBitmap(@RequestBody Bitmap bitmap) {
        log.debug("Entered class = RestfulAndroidJavaController & method = handlePostCar & Bitmap bitmap = {}", bitmap);
        return server.send(bitmap);
    }

    @GetMapping("records/EN/{englishName}")
    @ResponseStatus(HttpStatus.OK)
    public Plant handleGetRecordbyEnglishName(@PathVariable String englishName) {
        log.debug("Entered class = RestfulAndroidJavaController & method = handleGetRecordbyEnglishName & String englishName = {}", englishName);
        return repository.getRecordByEnglishName(englishName);
    }

    @GetMapping(value = "records/LAT/{latinName}",  consumes = "application/text;charset=UTF-8")
    @ResponseStatus(HttpStatus.OK)
    public Plant handleGetRecordbyLatinName(@PathVariable String latinName) {
        log.debug("Entered class = RestfulAndroidJavaController & method = handleGetRecordbyLatinName & String latinName = {}", latinName);
        return repository.getRecordByLatinName(latinName);
    }

    @PostMapping("records")
    @ResponseStatus(HttpStatus.OK)
    public List<Plant> handleGetRecords(@RequestBody Bounds interval) {
        log.debug("Entered class = RestfulAndroidJavaController & method = handleGetRecords & Bounds interval = {}", interval);
        return repository.getPagedRecords(interval);
    }
}
