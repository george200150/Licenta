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
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

// HAVE DONE TO SETUP:
// -> created new DIRECT EXCHANGE "PythonExchange.IN" from UI
// -> BOUND to "Licenta.PythonQueue" the created exchange with ROUTING KEY "to.python.routing.key"
// -> now it works

@Slf4j
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
        log.debug("Entered class = QueueProxy & method = send & Bitmap bitmap = {}", bitmap);
        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime()); // TODO: maybe add to temp map <token, client_info>

        Message message = new Message();
        message.setBitmap(bitmap);
        message.setToken(token);

        try {
            log.debug("Entered try in send & Message message = {}", message);
            String json = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(message);

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

            log.debug("Exit try in handlePythonMessage");
        } catch (JsonProcessingException e) {
            log.debug("Throw in handlePythonMessage & JsonProcessingException e = {}", e);
            throw new QueueProxyException(e);
        }
        log.debug("Exit class = QueueProxy & method = handlePythonMessage & return = void");
    }
}
