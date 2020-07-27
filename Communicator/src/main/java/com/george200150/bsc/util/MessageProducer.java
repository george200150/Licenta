package com.george200150.bsc.util;

import com.george200150.bsc.exception.CustomRabbitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageProducer {

    @Autowired
    private RabbitTemplate erpRabbitTemplate;

    public void post(String routingKey, Object object) {
        log.debug("Entered class = MessageProducer & method = post & String routingKey = {}, Object object = {}", routingKey, object);
        try {
            log.debug("Entered try in post");
            erpRabbitTemplate.convertAndSend(routingKey, object);
            log.debug("Exit try in post");
        } catch (final Exception e) {
            log.debug("Throw in post & final Exception e = {}", e);
            throw new CustomRabbitException(e);
        }
        log.debug("Exit class = MessageProducer & method = post & return = void");
    }
}
