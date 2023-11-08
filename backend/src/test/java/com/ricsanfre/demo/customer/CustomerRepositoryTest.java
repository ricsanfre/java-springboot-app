package com.ricsanfre.demo.customer;

import com.ricsanfre.demo.AbstractTestcontainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// Disable embedded database and use Testcontainer extending from AbstractTestcontainersUnitTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainersUnitTest {
    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.save(customer);

        int customerId = underTest.findAll().stream().filter(c -> c.getEmail().equals(email))
                .findFirst().map(c -> c.getId()).orElseThrow();
        // When
        boolean actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenNotExisting() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        // When
        boolean actual = underTest.existsCustomerByEmail(email);
        // Then
        assertThat(actual).isFalse();
    }
    @Test
    void getCustomerByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.save(customer);

        int customerId = underTest.findAll().stream().filter(c -> c.getEmail().equals(email))
                .findFirst().map(c -> c.getId()).orElseThrow();
        // When
        Optional<Customer> actual = underTest.getCustomerByEmail(email);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getPassword()).isEqualTo(customer.getPassword());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }

    @Test
    void getCustomerByEmailFailsWhenNotExistingMail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        Optional<Customer> actual = underTest.getCustomerByEmail(email);
        // Then
        assertThat(actual).isNotPresent();
    }

    @Test
    void canSetProfileImageId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        String profileImageId = UUID.randomUUID().toString();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());

        underTest.save(customer);
        int customerId = underTest.findAll().stream().filter(c -> c.getEmail().equals(email))
                .findFirst().map(c -> c.getId()).orElseThrow();
        // When
        underTest.setProfileImageId(profileImageId,customerId);
        // Then
        Optional<Customer> actual = underTest.findById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getProfileImageId()).isEqualTo(profileImageId);
        });

    }
}