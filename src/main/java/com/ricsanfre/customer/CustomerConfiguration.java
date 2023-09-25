package com.ricsanfre.customer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CustomerConfiguration {

    // How to get application property from application.yml
    // app.useFakeRepository -> default value is false
    // @Value("${app.useFakeRepository:false}")
    // private boolean useFakeRepository;

    // CommandLineRunner Executing code on startup
    @Bean
    CommandLineRunner customerDBInitialLoad(CustomerRepository customerRepository) {
        return args -> {
            // Insert some Customer records into database on startup
            Customer alex = new Customer("alex", "s1cret0", "alex@mail.com", 21);
            Customer juan = new Customer("juan", "s1cret0", "juan@mail.com", 27);
            List<Customer> customers = List.of(alex, juan);
            customerRepository.saveAll(customers);
        };
    }
}
