package com.ricsanfre.customer;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<TableClass,IdClass>
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    boolean existsCustomerByEmail(String email);
    Customer getCustomerByEmail(String email);
}
