package com.george200150.bsc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.model.*;
import com.george200150.bsc.persistence.PlantDataBaseRepository;
import com.george200150.bsc.util.MessageProducer;
import com.george200150.bsc.util.ParseBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Arrays;
import java.util.List;

// HAVE DONE TO SETUP:
// -> created new DIRECT EXCHANGE "PythonExchange.IN" from UI
// -> BOUND to "Licenta.PythonQueue" the created exchange with ROUTING KEY "to.python.routing.key"
// -> now it works

public class QueueProxy {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MessageProducer producer;

    @Autowired
    private PlantDataBaseRepository repository;

    @Value("${spring.queues.routing.send}") // http://localhost:15672/#/queues/%2F/Licenta.PythonQueue
    private String routingKey;

    public Token send(Bitmap bitmap) {
        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime()); // TODO: maybe add to temp map <token, client_info>

        Message message = new Message();
        message.setBitmap(bitmap);
        message.setToken(token);

        try {
            String json = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);
            // probabil asta e eroare pentru ca nu am pus Validator bean-ul in config

            System.out.println(json);
            System.out.println("routingKey = " + routingKey);

            producer.post(routingKey, json); // TODO: THIS THROWS CustomRabbitException IN CASE QUEUE HAS A PROBLEM !!!

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // TODO: THROW NEW CUSTOM EXCEPTION !!!
        }
        return token;
    }


    @RabbitListener(queues = "${spring.queues.name.receive}") // http://localhost:15672/#/queues/%2F/Licenta.JavaQueue
    public void handlePythonMessage(@Payload final byte[] byteMessage) {

        System.out.println(Arrays.toString(byteMessage));
        String jsonMessage = new String(byteMessage);
        BackMessage backMessage = null;
        try {
            backMessage = mapper.readValue(jsonMessage, BackMessage.class);

            List<Prediction> predictions = backMessage.getPreds();
            Token token = backMessage.getToken();

            // TODO: create push notification for map[token] client

            System.out.println("PREDICTIONS = " + predictions);

            String text = ParseBuilder.parse(predictions);

            System.out.println(text);

            Plant plant = repository.getRecordByLatinName(text);

            System.out.println(plant);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
