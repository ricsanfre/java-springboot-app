package com.ricsanfre.demo.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    private CustomerRowMapper underText;
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        underText = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {

        // Given
        Customer expectedCustomer = new Customer (1,"John Doe", "123","foo@example.com", 21, Gender.MALE);

        Mockito.when(resultSet.getInt("id")).thenReturn(expectedCustomer.getId());
        Mockito.when(resultSet.getString("name")).thenReturn(expectedCustomer.getName());
        Mockito.when(resultSet.getString("password")).thenReturn(expectedCustomer.getPassword());
        Mockito.when(resultSet.getString("email")).thenReturn(expectedCustomer.getEmail());
        Mockito.when(resultSet.getInt("age")).thenReturn(expectedCustomer.getAge());
        Mockito.when(resultSet.getString("gender")).thenReturn(expectedCustomer.getGender().name());

        // When
        Customer customer = underText.mapRow(resultSet, 1);

        // Then
        assertThat(customer).isEqualTo(expectedCustomer);

    }
}