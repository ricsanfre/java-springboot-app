package com.ricsanfre.infoapp;

import com.ricsanfre.customer.Customer;
import com.ricsanfre.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InfoAppConfiguration {
    @Bean
    CommandLineRunner printInfoAppProperties(InfoApp infoApp) {
        return args -> {
            System.out.println(infoApp);
        };
    }
}
