package com.ricsanfre.demo.infoapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfoAppConfiguration {
    @Bean
    CommandLineRunner printInfoAppProperties(InfoApp infoApp) {
        return args -> {
            System.out.println(infoApp);
        };
    }
}
