package com.ricsanfre.demo.customer;

import com.ricsanfre.demo.AbstractTestcontainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainersUnitTest {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        // Create new instance for each test
        // JdbCTemplate is not autoconfigured for us. We need to create it manually
        underTest = new CustomerJDBCDataAccessService(getJdbcTemplate(), customerRowMapper);
    }

    @Test
    void getAllCustomers() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20,
                Gender.randomGender());
        underTest.insertCustomer(customer);
        // When
        List<Customer> actual = underTest.getAllCustomers();
        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void getCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.insertCustomer(customer);
        // When
        int customerId = underTest.getCustomerByEmail(email).orElseThrow().getId();

        Optional<Customer> actual = underTest.getCustomerById(customerId);
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
    void returnEmptyWhenGetCustomerById() {

        int id = -1;
        Optional<Customer> actual = underTest.getCustomerById(id);
        assertThat(actual).isEmpty();

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
        underTest.insertCustomer(customer);

        int customerId = underTest.getAllCustomers().stream().filter(c -> c.getEmail().equals(email))
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
    void exitsCustomerWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.insertCustomer(customer);
        // When

        boolean actual = underTest.exitsCustomerWithEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void exitsCustomerWithEmailFailsWhenDoesNotExist() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        // When
        boolean actual = underTest.exitsCustomerWithEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void exitsCustomerWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.insertCustomer(customer);
        Integer id = underTest.getCustomerByEmail(email).orElseThrow().getId();
        // When
        boolean actual = underTest.exitsCustomerWithId(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void exitsCustomerWithIdFailsWhenDoesNotExist() {
        // Given
        Integer id = -1;
        // When
        boolean actual = underTest.exitsCustomerWithId(id);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.insertCustomer(customer);
        Integer id = underTest.getCustomerByEmail(email).orElseThrow().getId();
        // When
        underTest.deleteCustomerById(id);

        boolean actual = underTest.exitsCustomerWithId(id);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        String password = FAKER.internet().password();
        Gender gender = Gender.randomGender();
        int age = 20;
        Customer customer = new Customer(
                name,
                password,
                email,
                age,
                gender);
        underTest.insertCustomer(customer);

        Customer insertedCustomer = underTest.getCustomerByEmail(email).orElseThrow();

        // When
        String newName = "foo";
        insertedCustomer.setName(newName);
        underTest.updateCustomer(insertedCustomer);
        Optional<Customer> actual = underTest.getCustomerByEmail(email);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(insertedCustomer.getId());
            assertThat(c.getEmail()).isEqualTo(insertedCustomer.getEmail());
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getPassword()).isEqualTo(customer.getPassword());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });

    }
    @Test
    void updateCustomerPassword() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        String password = FAKER.internet().password();
        int age = 20;
        Gender gender = Gender.randomGender();
        Customer customer = new Customer(
                name,
                password,
                email,
                age,
                gender);
        underTest.insertCustomer(customer);

        Customer insertedCustomer = underTest.getCustomerByEmail(email).orElseThrow();

        // When
        String newPassword = "foo";
        insertedCustomer.setPassword(newPassword);
        underTest.updateCustomer(insertedCustomer);
        Optional<Customer> actual = underTest.getCustomerByEmail(email);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(insertedCustomer.getId());
            assertThat(c.getEmail()).isEqualTo(insertedCustomer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getPassword()).isEqualTo(newPassword);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        String password = FAKER.internet().password();
        int age = 20;
        Gender gender = Gender.randomGender();
        Customer customer = new Customer(
                name,
                password,
                email,
                age,
                gender);
        underTest.insertCustomer(customer);

        Customer insertedCustomer = underTest.getCustomerByEmail(email).orElseThrow();

        // When
        insertedCustomer.setAge(21);
        underTest.updateCustomer(insertedCustomer);
        Optional<Customer> actual = underTest.getCustomerByEmail(email);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(insertedCustomer.getId());
            assertThat(c.getEmail()).isEqualTo(insertedCustomer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getPassword()).isEqualTo(customer.getPassword());
            assertThat(c.getAge()).isEqualTo(21);
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });

    }

    @Test
    void updateCustomerGender() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        String password = FAKER.internet().password();
        int age = 20;
        Gender gender = Gender.MALE;
        Customer customer = new Customer(
                name,
                password,
                email,
                age,
                gender);
        underTest.insertCustomer(customer);

        Customer insertedCustomer = underTest.getCustomerByEmail(email).orElseThrow();

        // When
        insertedCustomer.setGender(Gender.FEMALE);
        underTest.updateCustomer(insertedCustomer);
        Optional<Customer> actual = underTest.getCustomerByEmail(email);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(insertedCustomer.getId());
            assertThat(c.getEmail()).isEqualTo(insertedCustomer.getEmail());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getPassword()).isEqualTo(customer.getPassword());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(Gender.FEMALE);
        });

    }
    @Test
    void updateCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        String password = FAKER.internet().password();
        int age = 20;
        Gender gender = Gender.randomGender();
        Customer customer = new Customer(
                name,
                password,
                email,
                age,
                gender);
        underTest.insertCustomer(customer);

        Customer insertedCustomer = underTest.getCustomerByEmail(email).orElseThrow();

        // When
        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        insertedCustomer.setEmail(newEmail);
        underTest.updateCustomer(insertedCustomer);
        Optional<Customer> actual = underTest.getCustomerByEmail(newEmail);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(insertedCustomer.getId());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getPassword()).isEqualTo(customer.getPassword());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }
    @Test
    void deleteAll() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().password(),
                email,
                20,
                Gender.randomGender());
        underTest.insertCustomer(customer);

        // When
        underTest.deleteAll();

        int actual = underTest.getAllCustomers().size();
        //Then
        assertThat(actual).isEqualTo(0);
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
        underTest.insertCustomer(customer);

        Customer insertedCustomer = underTest.getCustomerByEmail(email).orElseThrow();

        int customerId = insertedCustomer.getId();
        // When
        underTest.updateCustomerProfileImageId(customerId, profileImageId);
        // Then
        Optional<Customer> actual = underTest.getCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getProfileImageId()).isEqualTo(profileImageId);
        });

    }
}