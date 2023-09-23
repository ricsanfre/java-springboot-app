package com.ricsanfre;

import com.ricsanfre.customer.Customer;
import com.ricsanfre.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

}
