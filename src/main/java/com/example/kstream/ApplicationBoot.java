package com.example.kstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ApplicationBoot extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApplicationBoot.class);
    }

    public static void main(String[] args) { // NOSONAR - C'est Spring qui contrôle les paramètres
        SpringApplication.run(ApplicationBoot.class, args);
    }
}
