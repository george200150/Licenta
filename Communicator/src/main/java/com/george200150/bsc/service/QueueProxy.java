package com.george200150.bsc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.exception.CustomRabbitException;
import com.george200150.bsc.exception.QueueProxyException;
import com.george200150.bsc.model.*;
import com.george200150.bsc.persistence.PlantDataBaseRepository;
import com.george200150.bsc.util.MessageProducer;
import com.george200150.bsc.util.ParseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private PlantDataBaseRepository repository;

    @Value("${spring.queues.routing.send}") // http://localhost:15672/#/queues/%2F/Licenta.PythonQueue
    private String routingKey;

    public Token send(Bitmap bitmap) {
        log.debug("Entered class = QueueProxy & method = send & Bitmap bitmap = {}", bitmap);
        // TODO: Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        //  does not work... apparently
        // Token token = new Token("_TOKEN_");

        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setBitmap(bitmap);
        forwardMessage.setToken(token);

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
        BackMessage backMessage;
        try {
            log.debug("Entered try in handlePythonMessage & String jsonMessage = {}", jsonMessage);
            backMessage = mapper.readValue(jsonMessage, BackMessage.class);

            List<Prediction> predictions = backMessage.getPreds();
            Token token = backMessage.getToken();

            log.debug("received predictions in handlePythonMessage & List<Prediction> predictions = {}", predictions);
            String text = ParseBuilder.parse(predictions);
            log.debug("built text from predictions in handlePythonMessage & String text = {}", text);
            Plant plant = repository.getRecordByLatinName(text);
            log.debug("retrieved plant from DB in handlePythonMessage & Plant plant = {}", plant);

            // TODO: create push notification for map[token] client
            sendToToken(token);

            log.debug("Exit try in handlePythonMessage");
        } catch (JsonProcessingException e) {
            log.debug("Throw in handlePythonMessage & JsonProcessingException e = {}", e);
            throw new QueueProxyException(e);
        }
        log.debug("Exit class = QueueProxy & method = handlePythonMessage & return = void");
    }

    public void sendToToken(Token token) { // TODO: use token to submit message to topic = "TOKEN"
        // [START send_to_token]
        // This registration token comes from the client FCM SDKs.
        //String registrationToken = "dSV-dAHPRtySIGd75u_C7V:APA91bHkep2rgZOJrRERFhrwV1FiDtTE9anGU-T02aODoyJoU4o0yV26XjjC_9Qtoi8EzFWr43jvRVTHIExSmHDNUeM-2qtiJLTgbgfBJhL--kWrPt5vRHV0lzJYI6-XATPQ9vbGTwC6";

//        String TOPIC = token.getMessage();
//
//        JSONObject body = new JSONObject();
//        body.put("to", "/topics/" + TOPIC);
//        body.put("priority", "high");
//
//        JSONObject notification = new JSONObject();
//        notification.put("title", "Your search result is here!");
//        notification.put("body", "Acer");
//
//        JSONObject data = new JSONObject();
//        data.put("TOPIC", token.getMessage());
//
//        body.put("notification", notification);
//        body.put("data", data);
//
//
//        HttpEntity<String> request = new HttpEntity<>(body.toString());
//
//        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
//        CompletableFuture.allOf(pushNotification).join();
//        String firebaseResponse = "FAILED!!!";
//
//        try {
//            firebaseResponse = pushNotification.get();
//
//            //return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

//        System.out.println("Successfully sent message: " + firebaseResponse);
        // [END send_to_token]

        // TODO: +/- TESTING PURPOSES - CREATE (POST IN THE END) GET REQUEST FOR THIS URL: http://localhost:8080/send/_TOKEN_
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> httpEntity = new HttpEntity<String>(token.getMessage(), null);
        try {
            //ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/send/_TOKEN_", HttpMethod.GET, httpEntity, String.class);
            ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/send/" + token.getMessage(), HttpMethod.GET, httpEntity, String.class);
            // TODO: ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/send/" + token.getMessage(), HttpMethod.GET, httpEntity, String.class);
            //  does not work... or, the network does not work anymore
            System.out.println(response);
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
        }
    }
}
