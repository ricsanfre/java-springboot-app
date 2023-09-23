package com.ricsanfre.customer;

public record CustomerRegistrationRequest(
        String name,
        String password,
        String email,
        Integer age
) {

}
