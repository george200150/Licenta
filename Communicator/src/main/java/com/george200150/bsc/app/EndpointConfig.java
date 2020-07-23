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

//    @Bean
//    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
//        dataSourceBuilder.url("dbc:mysql://localhost:3306/licenta");
//        dataSourceBuilder.username("admin");
//        dataSourceBuilder.password("admin");
//        DataSource dataSource = dataSourceBuilder.build();
//        return new NamedParameterJdbcTemplate(dataSource);
//    }

//    @Bean
//    public DataSource dataSource() {
//        return DataSourceBuilder.create()
//                .driverClassName("com.mysql.jdbc.Driver")
//                .url("dbc:mysql://localhost:3306/licenta")
//                .username("admin")
//                .password("admin")
//                .build();
//    }
//
//    @Bean
//    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
//        return new NamedParameterJdbcTemplate(dataSource);
//    }
}
