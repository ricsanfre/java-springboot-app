package com.ricsanfre.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {

}
