package com.ricsanfre.demo.customer;

public record CustomerUpdateRequest(
        String name,
        String password,
        String email,
        Integer age,
        String gender
) {
}
