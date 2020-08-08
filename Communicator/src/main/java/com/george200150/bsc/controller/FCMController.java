package com.george200150.bsc.controller;

import com.george200150.bsc.service.AndroidPushNotificationsService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class FCMController {

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @RequestMapping(value = "/send/{token}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> send(@PathVariable("token") String token) throws JSONException {

        String TOPIC = token;

        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + TOPIC);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "Your search result is here!");
        notification.put("body", "Acer"); // TODO: THIS IS HARDCODED !!!!!!

        JSONObject data = new JSONObject();
        data.put("TOPIC", token);
        System.out.println(token);

        body.put("notification", notification);
        body.put("data", data);

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        // TODO : MUST DELAY THE SEND IN ORDER FOR THE CLIENT TO HAVE TIME TO SUBSCRIBE !!! (I GUESS...)
        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();

            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
    }
}