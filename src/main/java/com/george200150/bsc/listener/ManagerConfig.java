package com.george200150.bsc.listener;

import com.george200150.bsc.service.QueueService;

public class ManagerConfig {

    public QueueService QueueService(){
        return new QueueService();
    }
}
