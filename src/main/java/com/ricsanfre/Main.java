package com.ricsanfre;

import com.ricsanfre.customer.Customer;
import com.ricsanfre.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    // Insert some Customer records into database on startup
    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            Customer alex = new Customer("alex","alex@mail.com", 21);
            Customer juan = new Customer("juan","juan@mail.com", 27);
            List<Customer> customers = List.of(alex, juan);
            customerRepository.saveAll(customers);
        };
    }


}
