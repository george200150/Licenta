package com.george200150.bsc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.exception.CustomRabbitException;
import com.george200150.bsc.exception.PushNotificationException;
import com.george200150.bsc.exception.QueueProxyException;
import com.george200150.bsc.model.*;
import com.george200150.bsc.util.MessageProducer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

// HAVE DONE TO SETUP:
// -> created new DIRECT EXCHANGE "PythonExchange.IN" from UI
// -> BOUND to "Licenta.PythonQueue" the created exchange with ROUTING KEY "to.python.routing.key"
// -> now it works

@Slf4j
public class QueueProxy {
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessageProducer producer;

    @Value("${spring.queues.routing.send}") // http://localhost:15672/#/queues/%2F/Licenta.PythonQueue
    private String routingKey;

    public Token send(ForwardMessage forwardMessage) {
        log.debug("Entered class = QueueProxy & method = send & ForwardMessage forwardMessage = {}", forwardMessage);
        Token token = forwardMessage.getToken();

        try {
            log.debug("Entered try in send & ForwardMessage forwardMessage = {}", forwardMessage);
            String json = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(forwardMessage);

            log.debug("Extracted JSON in send & String json = {}", json);

            producer.post(routingKey, json); // THIS THROWS CustomRabbitException IN CASE QUEUE HAS A PROBLEM
            log.debug("Exiting try after producer.post(routingKey, json); in send");
        } catch (JsonProcessingException | CustomRabbitException e) {
            log.debug("Throw in send & JsonProcessingException e = {}", e);
            throw new QueueProxyException(e);
        }
        log.debug("Exiting class = QueueProxy & method = send & return Token token = {}", token);
        return token;
    }

    @RabbitListener(queues = "${spring.queues.name.receive}") // http://localhost:15672/#/queues/%2F/Licenta.JavaQueue
    public void handlePythonMessage(@Payload final byte[] byteMessage) {
        log.debug("Entered class = QueueProxy & method = handlePythonMessage & final byte[] byteMessage = {}", byteMessage);

        String jsonMessage = new String(byteMessage);
        BackMessage backMessage; // TODO: BackMessage should now contain a Photo
        try {
            log.debug("Entered try in handlePythonMessage & String jsonMessage = {}", jsonMessage);
            backMessage = mapper.readValue(jsonMessage, BackMessage.class);

            List<Pixel> predictedImage = backMessage.getPreds();
            Token token = backMessage.getToken();

            log.debug("received predictions in handlePythonMessage & List<Prediction> predictedImage = {}", predictedImage);
//            String text = ParseBuilder.parse(predictedImage);
//            log.debug("built text from predictions in handlePythonMessage & String text = {}", text);
//            Plant plant = repository.getRecordByLatinName(text);
//            log.debug("retrieved plant from DB in handlePythonMessage & Plant plant = {}", plant);

//            sendImageAndToken(plant, token); // THIS THROWS PushNotificationException IN CASE PUSH NOTIFICATION HAS A PROBLEM
            sendImageAndToken(predictedImage, token); // TODO: refactor this for SS/DE

            log.debug("Exit try in handlePythonMessage");
        } catch (JsonProcessingException | PushNotificationException e) {
            log.debug("Throw in handlePythonMessage & JsonProcessingException | PushNotificationException e = {}", e);
            throw new QueueProxyException(e);
        }
        log.debug("Exit class = QueueProxy & method = handlePythonMessage & return = void");
    }

//    public void sendImageAndToken(Plant plant, Token token) {
    public void sendImageAndToken(List<Pixel> predictedImage, Token token) {
//        log.debug("Entered class = QueueProxy & method = sendImageAndToken & Plant plant = {} & Token token = {}", plant, token);
        ///////////////////////////////////////////////////////// TODO: maybe refactor to a message json builder ???
        String TOPIC = token.getMessage();

        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + TOPIC);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "Your search result is here!");
//        notification.put("body", plant.getEnglishName());

        JSONObject data = new JSONObject();
        data.put("TOPIC", token);
//        data.put("PLANT", predictedImage.toString());
//        data.put("PLANT", "YEE_HAAAAAAA");

        String json = new Gson().toJson(predictedImage);
        data.put("PLANT", json);


        System.out.println(token);

        body.put("notification", notification);
        body.put("data", data);
        log.debug("created JSONObject body = {}", body);


        HttpEntity<String> request = new HttpEntity<>(body.toString());
        log.debug("created HttpEntity<String> request = {}", request);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);

        // TODO: could UPLOAD the PHOTO on the internet, and NOTIFY the user with the image's LINK...
        // 1000 pixel images are too big (needed size == 187500)
        // TODO: Caused by: org.springframework.web.client.HttpClientErrorException$BadRequest: 400 Bad Request: [{"error":"MessageTooBig"}]
        // Check that the total size of the payload data included in a message does not exceed FCM limits: 4096 bytes for most messages, or 2048 bytes in the case of messages to topics. This includes both the keys and the values

        CompletableFuture.allOf(pushNotification).join();
        log.debug("called androidPushNotificationsService.send(request) & CompletableFuture<String> pushNotification = {}", pushNotification);
        try {
            log.debug("Entered try in sendImageAndToken");
            String firebaseResponse = pushNotification.get();
            log.debug("Exiting try after String firebaseResponse = pushNotification.get(); in sendImageAndToken & String firebaseResponse = {}", firebaseResponse);
        } catch (InterruptedException | ExecutionException e) {
            log.debug("Throw in sendImageAndToken & InterruptedException | ExecutionException e = {}", e);
            throw new PushNotificationException(e);
        }
        log.debug("Exit class = QueueProxy & method = sendImageAndToken & return = void");
    }
}
