package com.ricsanfre.demo.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.ricsanfre.demo.customer.Customer;
import com.ricsanfre.demo.customer.CustomerRegistrationRequest;
import com.ricsanfre.demo.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
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

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, password, email, age
        );

        // 2 - send POST request
        webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // 3 - get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // 4 - make sure the customer is present
        Customer expectedCustomer = new Customer(name, password, email, age);

        // Ignore in the comparison Id and password
        // (password is not returned in the getCustomers response: @JsonIgnore annotation
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "password")
                .contains(expectedCustomer);

        // 5 - get customer by Id

        int id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst().orElseThrow();

        expectedCustomer.setId(id);

        Customer actual = webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
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

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, password, email, age
        );

        // 2 - send POST request
        webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // 3 - get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // 4 - get customer by Id

        int id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst().orElseThrow();

        // 5 - delete customer

        webTestClient.delete()
                .uri(customerURI+ "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // 6 - get customer
        webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
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

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, password, email, age
        );

        // 2 - send POST request
        webTestClient.post()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // 3 - get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // 5 - get customer by Id

        int id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst().orElseThrow();

        // 6 - Create Update request
        String newName = faker.name().fullName();
        Integer newAge = RANDOM.nextInt(15,99);
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null, newAge
        );

        // 7 - Update Customer

        webTestClient.put()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // 8 - Get customer

        Customer expectedCustomer = new Customer(id, newName, password, email, newAge);
        expectedCustomer.setId(id);

        Customer actual = webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // Check ignoring password field (not returned in the response)
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(expectedCustomer);

    }
}
