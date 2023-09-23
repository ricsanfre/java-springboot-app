package com.ricsanfre.customer;

public record CustomerUpdateRequest(
        String name,
        String password,
        String email,
        Integer age
) {
}
