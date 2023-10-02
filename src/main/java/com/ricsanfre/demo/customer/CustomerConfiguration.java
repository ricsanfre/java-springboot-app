package com.ricsanfre.demo.customer;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

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
            // Insert some random Customer record into database on startup
            // Using JavaFaker and Random
            Faker faker = new Faker();
            Random random = new Random();
            Customer customer = new Customer(
                    faker.name().firstName(),
                    faker.internet().password(),
                    faker.internet().emailAddress(),
                    random.nextInt(16,99));
            customerRepository.save(customer);
        };
    }
}
