package com.ricsanfre.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.ToString;

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

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

}
