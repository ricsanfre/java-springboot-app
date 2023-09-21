package com.ricsanfre.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
