package com.ricsanfre.customer;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Integer id);

}