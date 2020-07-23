package com.george200150.bsc.listener;

import com.george200150.bsc.model.BackMessage;
import com.george200150.bsc.model.Bitmap;
import com.george200150.bsc.model.Message;
import com.george200150.bsc.model.Prediction;
import com.george200150.bsc.manager.MessageManager;
import com.george200150.bsc.service.QueueService;
import com.george200150.bsc.util.MessageProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;

public class MQManager {

//    @Value("${spring.routingKeys.pickConfirm}")
//    private String routingKey;
//
//    @Autowired
//    private MessageProducer messageProducer;
//
//    @Autowired
//    private QueueService service;
//
//    private MessageManager messageManager;
//
//    public MQManager(MessageManager messageManager) {
//        this.messageManager = messageManager;
//    }
//
//    @RabbitListener(queues = "${garmin.queues.OTHER_QUEUE}")
//    public void handleInboundMessage(@Payload final BackMessage backMessage) { // this must be extended according to the business architecture
//        //messageManager.handlePickConfirm(preds);
//        service.putNewMessage(backMessage.getToken(), backMessage);
//    }
//
//
//    public void postOntoQueue(Message message) {
//        messageProducer.post(routingKey, message);
//    }
//
//    public List<Prediction> listen(String token) {
//        service.subscribeConsumer(token);
//
//        List<Prediction> predictions = service.waitForMyMessage();
//        return predictions;
//    }
}
