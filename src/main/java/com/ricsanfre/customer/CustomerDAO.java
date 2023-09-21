package com.ricsanfre.customer;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean exitsCustomerWithEmail(String email);

    boolean exitsCustomerWithId(Integer id);
    void deleteCustomerById(Integer id);

    void updateCustomer(Customer customer);
}