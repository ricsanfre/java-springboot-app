package com.ricsanfre.demo.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    @Mock
    private CustomerRepository customerRepository;

    private AutoCloseable autoCloseable;
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();
        // Then
        // Verify that findAll() method in the Mock is executed
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById() {
        // When
        Integer id = 1;
        underTest.getCustomerById(id);
        // Then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void getCustomerByEmail() {
        // When
        String email = "foo@mail.com";
        underTest.getCustomerByEmail(email);
        // Then
        Mockito.verify(customerRepository).getCustomerByEmail(email);
    }

    @Test
    void insertCustomer() {
        // When
        Customer customer = new Customer("foo","123","foo@mail.com",18);
        underTest.insertCustomer(customer);
        // Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void exitsCustomerWithEmail() {
        // When
        String email = "foo@mail.com";
        underTest.exitsCustomerWithEmail(email);
        // Then
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void exitsCustomerWithId() {
        // When
        Integer id = 1;
        underTest.exitsCustomerWithId(id);
        // Then
        Mockito.verify(customerRepository).existsById(id);
    }

    @Test
    void deleteCustomerById() {
        // When
        Integer id = 1;
        underTest.deleteCustomerById(id);
        // Then
        Mockito.verify(customerRepository).deleteById(id);

    }

    @Test
    void updateCustomer() {
        // When
        Customer customer = new Customer("foo","123","foo@mail.com",18);
        underTest.updateCustomer(customer);
        // Then
        Mockito.verify(customerRepository).save(customer);

    }

    @Test
    void deleteAll() {
        // When
        underTest.deleteAll();
        //Then
        Mockito.verify(customerRepository).deleteAll();
    }
}