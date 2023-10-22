package com.ricsanfre.demo.customer;

import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerDTOMapper implements Function<Customer,CustomerDTO> {

    @Override
    public CustomerDTO apply(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender(),
                customer.getEmail(),
                customer.getAuthorities()
                        .stream()
                        .map(r->r.getAuthority())
                        .collect(Collectors.toList())
        );
    }
}
