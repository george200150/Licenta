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

    @PostMapping("bitmap")
    @ResponseStatus(HttpStatus.OK)
    public Token handlePostBitmap(@RequestBody ForwardMessage forwardMessage) {
        log.debug("Entered class = RestfulAndroidJavaController & method = handlePostCar & ForwardMessage forwardMessage = {}", forwardMessage);
        return server.send(forwardMessage);
    }

    @PostMapping("fetch")
    @ResponseStatus(HttpStatus.OK)
    public Bitmap handleGetBitmap(@RequestBody String pathname) {
        pathname = pathname.substring(1, pathname.length()-1);
        log.debug("Entered class = RestfulAndroidJavaController & method = handleGetBitmap & String pathname = {}", pathname);
        return server.fetch(pathname);
    }
}
