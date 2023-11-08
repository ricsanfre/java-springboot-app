package com.ricsanfre.demo.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.beans.Transient;
import java.util.Optional;

// JpaRepository<TableClass,IdClass>
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    boolean existsCustomerByEmail(String email);

    Optional<Customer> getCustomerByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Customer c SET c.profileImageId = ?1 WHERE c.id = ?2")
    @Transactional
    int setProfileImageId(String profileImageId, Integer customerId);

}
