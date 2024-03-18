package com.example.kstream.core;

import com.example.kstream.core.firstdomain.api.FirstService;
import com.example.kstream.core.firstdomain.service.DefaultFirstService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ServiceConfig {

    @Bean
    FirstService firstService(){
        return new DefaultFirstService();
    }

}
