package com.example.kstream.application;

import com.example.kstream.application.proxy.FirstServiceProxy;
import com.example.kstream.application.proxy.impl.DefaultFirstServiceProxy;
import com.example.kstream.core.firstdomain.api.FirstService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    FirstServiceProxy firstServiceProxy(final FirstService firstService){
        return new DefaultFirstServiceProxy(firstService);
    }

}
