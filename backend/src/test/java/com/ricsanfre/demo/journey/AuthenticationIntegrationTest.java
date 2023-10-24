package com.ricsanfre.demo.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.ricsanfre.demo.auth.AuthenticationRequest;
import com.ricsanfre.demo.auth.AuthenticationResponse;
import com.ricsanfre.demo.customer.CustomerDTO;
import com.ricsanfre.demo.customer.CustomerRegistrationRequest;
import com.ricsanfre.demo.customer.Gender;
import com.ricsanfre.demo.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    private static final Random RANDOM = new Random();

    private static final String customerURI = "api/v1/customer";
    private static final String authURI = "api/v1/auth/login";

    @Test
    void canLogin() {
        // Given
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String password = faker.internet().password();
        String email = fakerName.lastName() + "." + UUID.randomUUID() + "@example.com";
        Integer age = RANDOM.nextInt(16, 99);
        Gender gender = Gender.randomGender();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, password, email, age, gender.name()
        );

        AuthenticationRequest authRequest = new AuthenticationRequest(
                email, password
        );

        // 1 - Try to login. 403 error is expected
        webTestClient.post()
                .uri(authURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // 2 - Register customer
        webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Try to login again
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(authURI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();
        String jwtToken = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        AuthenticationResponse response = result.getResponseBody();

        CustomerDTO customerDTO = response.customerDTO();

        assertThat(jwtUtil.isValidToken(jwtToken,customerDTO.userName())).isTrue();

        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.userName()).isEqualTo(email);
        assertThat(customerDTO.userRoles()).isEqualTo(List.of("ROLE_USER"));


    }
}
