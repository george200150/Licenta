package com.george200150.bsc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.model.*;
import com.george200150.bsc.util.MessageProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

public class QueueProxy {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessageProducer producer;

    @Value("${spring.routingKeys.send}") // http://localhost:15672/#/queues/%2F/Licenta.PythonQueue
    private String routingKey;

    public Token send(Bitmap bitmap) {
        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime()); // TODO: maybe add to temp map <token, client_info>

        Message message = new Message();
        message.setBitmap(bitmap);
        message.setToken(token);

        try {
            String json = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);

            System.out.println(json);

            producer.post(routingKey, json); // TODO: THIS THROWS CustomRabbitException IN CASE QUEUE HAS A PROBLEM !!!
            // PRODUCES: java.lang.IllegalArgumentException: SimpleMessageConverter only supports String, byte[] and Serializable payloads, received: com.george200150.bsc.model.Message

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO: THROW NEW CUSTOM EXCEPTION !!!
        }
        return token;
    }


    @RabbitListener(queues = "${spring.queueName.receive}") // http://localhost:15672/#/queues/%2F/Licenta.JavaQueue
    public void handlePythonMessage(@Payload final BackMessage backMessage) {

        List<Prediction> predictions = backMessage.getPreds();
        Token token = backMessage.getToken();

        // TODO: create push notification for map[token] client

        System.out.println(predictions);
    }
}
