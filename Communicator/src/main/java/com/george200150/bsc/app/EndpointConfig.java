package com.george200150.bsc.app;

import com.george200150.bsc.persistence.PlantDataBaseRepository;
import com.george200150.bsc.service.QueueProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

    @Bean
    public PlantDataBaseRepository plantDataBaseRepository(){
        return new PlantDataBaseRepository();
    }

    @Bean
    public QueueProxy queueProxy(){
        return new QueueProxy ();
    }
}
