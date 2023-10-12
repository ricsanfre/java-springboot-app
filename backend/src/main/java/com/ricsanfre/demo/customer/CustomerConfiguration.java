package com.ricsanfre.demo.customer;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    faker.internet().password(),
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com",
                    random.nextInt(16,99));
            customerRepository.save(customer);
        };
    }
}
