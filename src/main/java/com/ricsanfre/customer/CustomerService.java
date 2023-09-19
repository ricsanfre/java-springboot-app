package com.ricsanfre.customer;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomerService {

    private final CustomerDAO customerDAO;
    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        return customerDAO.getCustomerById(id)
                .orElseThrow(()-> new IllegalArgumentException("customer with id [%s] is not found".formatted(id)));
    }
}
