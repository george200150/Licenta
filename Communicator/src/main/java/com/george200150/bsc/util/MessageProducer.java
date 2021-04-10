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
        try {
            erpRabbitTemplate.convertAndSend(routingKey, object);
        } catch (final Exception e) {
            throw new CustomRabbitException(e);
        }
    }
}
