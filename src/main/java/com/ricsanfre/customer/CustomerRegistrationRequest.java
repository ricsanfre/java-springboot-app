package com.ricsanfre.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CustomerRegistrationRequest {

    @NotBlank(message = "name must be not empty")
    private final String name;
    @NotBlank(message = "password must be not empty")
    private final String password;
    @NotBlank(message = "email must be not empty")
    @Email(message = "email must have proper format")
    private final String email;

    private final Integer age;

    public CustomerRegistrationRequest(String name, String password, String email, Integer age) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.age = age;
    }

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

    @Override
    public String toString() {
        return "CustomerRegistrationRequest{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
