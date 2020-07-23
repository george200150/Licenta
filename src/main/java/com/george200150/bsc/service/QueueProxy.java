package com.george200150.bsc.service;

import com.george200150.bsc.model.*;
import com.george200150.bsc.util.MessageProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

public class QueueProxy {

    @Autowired
    private MessageProducer producer;

    @Value("${spring.routingKeys.toPythonQueue}")
    private String routingKey;

    public Token send(Bitmap bitmap) {
        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime()); // TODO: maybe add to temp map <token, client_info>

        Message message = new Message();
        message.setBitmap(bitmap);
        message.setToken(token);

        producer.post(routingKey, message); // TODO: THIS THROWS CustomRabbitException IN CASE QUEUE HAS A PROBLEM !!!

        return token;
    }


    @RabbitListener(queues = "${spring.routingKeys.fromPythonQueue}")
    public void handlePythonMessage(@Payload final BackMessage backMessage) {

        List<Prediction> predictions = backMessage.getPreds();
        Token token = backMessage.getToken();

        // TODO: create push notification for map[token] client

        System.out.println(predictions);
    }
}
