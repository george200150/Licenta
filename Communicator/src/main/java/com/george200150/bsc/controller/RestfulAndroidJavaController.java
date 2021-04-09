package com.george200150.bsc.controller;

import com.george200150.bsc.model.*;
import com.george200150.bsc.service.QueueProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("data")
@RestController
public class RestfulAndroidJavaController {

    @Autowired
    private QueueProxy server;

//    @PostMapping(value = "simple",  consumes = "application/json;charset=UTF-8")
//    @ResponseStatus(HttpStatus.OK)
//    public Simple handlePostSimple(@RequestBody String simple) {
//        System.out.println(simple);
//        log.debug("Entered class = RestfulAndroidJavaController & method = handlePostSimple & Simple simple = {}", simple);
//        return new Simple("500");
//    }

    @PostMapping("bitmap")
    @ResponseStatus(HttpStatus.OK)
    public Token handlePostBitmap(@RequestBody ForwardMessage forwardMessage) {
        log.debug("Entered class = RestfulAndroidJavaController & method = handlePostCar & ForwardMessage forwardMessage = {}", forwardMessage);
        return server.send(forwardMessage);
    }
}
