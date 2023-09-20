package com.ricsanfre.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository("list")
public class CustomerArrayListDataAccessService implements CustomerDAO{

    private static final List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(1,"alex","alex@mail.com", 21);
        customers.add(alex);
        Customer juan = new Customer(2,"juan","juan@mail.com", 27);
        customers.add(juan);
    }
    @Override
    public List<Customer> getAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> getCustomerById(Integer id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }
}
