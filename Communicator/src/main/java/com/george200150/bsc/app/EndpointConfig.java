package com.george200150.bsc.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george200150.bsc.service.QueueProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public QueueProxy queueProxy() {
        return new QueueProxy();
    }
}
