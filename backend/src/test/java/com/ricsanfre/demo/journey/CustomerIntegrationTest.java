package com.ricsanfre.demo.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.ricsanfre.demo.customer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Spring-boot testing doc
// https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.testing
// Selecting RANDOM_PORT web environment to test with a running server
// testing full flesh application with the dev DataBase
// alternatively Testcontainers database could be used extending from AbstractTestcontainerUnitTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();

    private static final String customerURI = "api/v1/customer";

    @Test
    void canRegisterACustomer() {

        // 1 - create registration request
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

        // 2 - send POST request and get the Authorization header from response (JWT Token)
        String jwtToken = webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);
        // 3 - get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // 4 - get customer by Id

        int id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(c -> c.id())
                .findFirst().orElseThrow();

        // 5 - make sure the customer is present

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                age,
                gender,
                email,
                List.of("ROLE_USER"));

        assertThat(allCustomers)
                .contains(expectedCustomer);

        CustomerDTO actual = webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // Check ignoring password field (not returned in the response)
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteCustomerById() {

        // 1 - create registration request
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

        CustomerRegistrationRequest otherRequest = new CustomerRegistrationRequest(
                faker.name().fullName(),
                faker.internet().password(),
                fakerName.lastName() + "." + UUID.randomUUID() + "@example.com",
                77,
                Gender.randomGender().name()
        );

        // 2 - send POST request

        // Create customer to be deleted
        webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Create 2nd customer, whose JWT token will be used to delete.
        String jwtToken = webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(otherRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);
        // 3 - get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // 4 - get customer by Id

        int id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(c -> c.id())
                .findFirst().orElseThrow();

        // 5 - delete customer

        webTestClient.delete()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        // 6 - get customer
        webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void canUpdateCustomer() {
        // 1 - create registration request
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

        // 2 - send POST request
        String jwtToken = webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);
        // 3 - get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // 4 - get customer by Id

        int id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(c -> c.id())
                .findFirst().orElseThrow();

        // 6 - Create Update request
        String newName = faker.name().fullName();
        Integer newAge = RANDOM.nextInt(15, 99);
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null, newAge, null
        );

        // 7 - Update Customer

        webTestClient.put()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        // 8 - Get customer

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                newName,
                email,
                newAge,
                gender,
                email,
                List.of("ROLE_USER"));

        CustomerDTO actual = webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(actual)
                .isEqualTo(expectedCustomer);

    }
}
