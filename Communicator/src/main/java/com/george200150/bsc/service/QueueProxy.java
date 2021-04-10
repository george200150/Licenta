package com.george200150.bsc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.exception.CustomRabbitException;
import com.george200150.bsc.exception.PushNotificationException;
import com.george200150.bsc.exception.QueueProxyException;
import com.george200150.bsc.model.BackMessage;
import com.george200150.bsc.model.Bitmap;
import com.george200150.bsc.model.ForwardMessage;
import com.george200150.bsc.model.Token;
import com.george200150.bsc.persistence.ImageRepository;
import com.george200150.bsc.util.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        log.debug("Entered class = QueueProxy & method = send & THIS IS HOW THE DATA WILL BE SENT TO PYTHON FROM JAVA VIA MQ: ForwardMessage forwardMessage = {}", forwardMessage);
        Token token = forwardMessage.getToken();

        try {
//            log.debug("Entered try in send & ForwardMessage forwardMessage = {}", forwardMessage);
            String json = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(forwardMessage);

            log.debug("Extracted JSON in send & String json = {}", json);

            producer.post(routingKey, json); // THIS THROWS CustomRabbitException IN CASE QUEUE HAS A PROBLEM
            log.debug("Exiting try after producer.post(routingKey, json); in send");
        } catch (JsonProcessingException | CustomRabbitException e) {
            log.debug("Throw in send & JsonProcessingException e = { }", e);
            throw new QueueProxyException(e);
        }
        log.debug("Exiting class = QueueProxy & method = send & return Token token = {}", token);
        return token;
    }

    @RabbitListener(queues = "${spring.queues.name.receive}") // http://localhost:15672/#/queues/%2F/Licenta.JavaQueue
    public void handlePythonMessage(@Payload final byte[] byteMessage) {
        log.debug("Entered class = QueueProxy & method = handlePythonMessage & final byte[] byteMessage = too long, you know...");

        String jsonMessage = new String(byteMessage);
        BackMessage backMessage;
        try {
            log.debug("Entered try in handlePythonMessage & THIS IS HOW THE DATA WAS RECEIVED FROM PYTHON VIA MQ: String jsonMessage = {}", jsonMessage);
            backMessage = mapper.readValue(jsonMessage, BackMessage.class);
            int[] predictedImage = backMessage.getPreds();

            Token token = backMessage.getToken();

            // TODO: save the image in repository
            String imagePath = ImageRepository.saveImage(backMessage.getW(), backMessage.getH(), backMessage.getPreds());

//            log.debug("received predictions in handlePythonMessage & List<Prediction> predictedImage = {}", predictedImage);

            // THIS THROWS PushNotificationException IN CASE PUSH NOTIFICATION HAS A PROBLEM
            sendImageAndToken(imagePath, token);

            log.debug("Exit try in handlePythonMessage");
        } catch (JsonProcessingException | PushNotificationException e) {
            log.debug("Throw in handlePythonMessage & JsonProcessingException | PushNotificationException e = { }", e);
            throw new QueueProxyException(e);
        }
        log.debug("Exit class = QueueProxy & method = handlePythonMessage & return = void");
    }

    private void sendImageAndToken(String resourcePath, Token token) {
//        log.debug("Entered class = QueueProxy & predictedImage = sendImageAndToken & Plant plant = {} & Token token = {}", predictedImage, token);
        ///////////////////////////////////////////////////////// TODO: maybe refactor to a message json builder ???
        String TOPIC = token.getMessage();

        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + TOPIC);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "ONE processed image' pixel is here!");

        JSONObject data = new JSONObject();
        data.put("TOPIC", token);

        System.out.println("RESOURCE = " + resourcePath);
        data.put("RESOURCE", resourcePath);

        System.out.println(token);

        body.put("notification", notification);
        body.put("data", data);
//        log.debug("created JSONObject body = {}", body);
        HttpEntity<String> request = new HttpEntity<>(body.toString());
//        log.debug("created HttpEntity<String> request = {}", request);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);

        CompletableFuture.allOf(pushNotification).join();
        log.debug("called androidPushNotificationsService.send(request) & CompletableFuture<String> pushNotification = {}", pushNotification);
        try {
            log.debug("Entered try in sendImageAndToken");
            String firebaseResponse = pushNotification.get();
            log.debug("Exiting try after String firebaseResponse = pushNotification.get(); in sendImageAndToken & String firebaseResponse = {}", firebaseResponse);
        } catch (InterruptedException | ExecutionException e) {
            log.debug("Throw in sendImageAndToken & InterruptedException | ExecutionException e = { }", e);
            throw new PushNotificationException(e);
        }

        log.debug("Exit class = QueueProxy & method = sendImageAndToken & return = void");
    }

    public Bitmap fetch(String pathname) {
        log.debug("Entered class = QueueProxy & method = fetch & String pathname = {}", pathname);
        return ImageRepository.readImage(pathname);
    }
}
