package com.george200150.bsc.util;

import com.george200150.bsc.exception.CustomRabbitException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

//    @Autowired
//    private RabbitTemplate erpRabbitTemplate;
//
//    public void post(String routingKey, Object object) {
//        try {
//            erpRabbitTemplate.convertAndSend(routingKey, object);
//        } catch (final Exception e) {
//            e.printStackTrace();
//            throw new CustomRabbitException(e);
//        }
//    }
}
