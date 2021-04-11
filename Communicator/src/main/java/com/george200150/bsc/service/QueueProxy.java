package com.george200150.bsc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.exception.CustomRabbitException;
import com.george200150.bsc.exception.DangerousOperationError;
import com.george200150.bsc.exception.ImageLoadException;
import com.george200150.bsc.exception.ImageSaveException;
import com.george200150.bsc.exception.PushNotificationException;
import com.george200150.bsc.exception.QueueProxyException;
import com.george200150.bsc.model.BackMessage;
import com.george200150.bsc.model.Bitmap;
import com.george200150.bsc.model.ForwardMessage;
import com.george200150.bsc.model.Token;
import com.george200150.bsc.persistence.ImageDAO;
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
        Token token = forwardMessage.getToken();
        try {
            String json = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(forwardMessage);
            producer.post(routingKey, json);
        } catch (JsonProcessingException | CustomRabbitException e) {
            throw new QueueProxyException(e);
        }
        return token;
    }

    @RabbitListener(queues = "${spring.queues.name.receive}") // http://localhost:15672/#/queues/%2F/Licenta.JavaQueue
    public void handlePythonMessage(@Payload final byte[] byteMessage) {
        String jsonMessage = new String(byteMessage);
        BackMessage backMessage;
        try {
            backMessage = mapper.readValue(jsonMessage, BackMessage.class);
            Token token = backMessage.getToken();

            String imagePath = ImageDAO.saveImage(backMessage.getW(), backMessage.getH(), backMessage.getPreds());

            sendImageAndToken(imagePath, token);
        } catch (JsonProcessingException | PushNotificationException | ImageSaveException e) {
            throw new QueueProxyException(e);
        }
    }

    private void sendImageAndToken(String resourcePath, Token token) {
        HttpEntity<String> request = buildRequest(resourcePath, token);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();
        try {
            String firebaseResponse = pushNotification.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new PushNotificationException(e);
        }
    }

    private HttpEntity<String> buildRequest(String resourcePath, Token token) {
        String TOPIC = token.getMessage();

        JSONObject body = new JSONObject();
        body.put("to", "/topics/" + TOPIC);
        body.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "ONE processed image' pixel is here!");

        JSONObject data = new JSONObject();
        data.put("TOPIC", token);
        data.put("RESOURCE", resourcePath);

        body.put("notification", notification);
        body.put("data", data);
        return new HttpEntity<>(body.toString());
    }

    public Bitmap fetch(String pathname) {
        try {
            return ImageDAO.readImage(pathname);
        } catch (ImageLoadException | DangerousOperationError e) {
            throw new QueueProxyException(e);
        }
    }
}
