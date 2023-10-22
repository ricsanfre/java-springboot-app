package com.ricsanfre.demo.customer;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        Gender gender,
        String userName,
        List<String> userRoles
) {}
