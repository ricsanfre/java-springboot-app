package com.ricsanfre.demo.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CustomerRegistrationRequest {

    @NotBlank(message = "name must be not empty")
    private final String name;
    @NotBlank(message = "password must be not empty")
    private final String password;
    @NotBlank(message = "email must be not empty")
    @Email(message = "email must have proper format")
    private final String email;

    private final Integer age;

}
