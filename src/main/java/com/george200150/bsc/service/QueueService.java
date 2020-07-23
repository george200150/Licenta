package com.george200150.bsc.service;

import com.george200150.bsc.model.BackMessage;
import com.george200150.bsc.model.Prediction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueService {

    private Map<String, List<Prediction>> receivedMessages;

    public QueueService() {
        this.receivedMessages = new HashMap<>();
    }

    public void subscribeConsumer(String token) {
        receivedMessages.put(token, null);
    }

    public void putNewMessage(String token, BackMessage backMessage) {
        receivedMessages.put(token, null);
    }

    public List<Prediction> waitForMyMessage() {
        return null;
    }
}
