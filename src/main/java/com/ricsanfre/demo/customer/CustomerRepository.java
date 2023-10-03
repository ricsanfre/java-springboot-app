package com.ricsanfre.demo.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// JpaRepository<TableClass,IdClass>
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    boolean existsCustomerByEmail(String email);

    Customer getCustomerByEmail(String email);

}
