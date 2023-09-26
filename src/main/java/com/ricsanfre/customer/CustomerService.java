package com.ricsanfre.customer;

import com.ricsanfre.exception.CustomerAlreadyExistsException;
import com.ricsanfre.exception.RequestValidationException;
import com.ricsanfre.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerService {

    // Logger SLF4J (Interface)
    // private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    // DAO
    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        // LOGGER.info("getAllCustomers() starting");
        log.info("getAllCustomers() starting");
        return customerDAO.getAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        // LOGGER.info("getCustomerByID started: id [{}]",id);
        log.info("getCustomerByID started: id [{}]",id);
        return customerDAO.getCustomerById(id)
                .orElseThrow(
                        () -> {
                            ResourceNotFoundException resourceNotFoundException =
                                    new ResourceNotFoundException("customer with id [%s] is not found".formatted(id));
                            log.error("getCustomerById(): error getting customer {}", id,resourceNotFoundException );
                            // LOGGER.error("getCustomerById(): error getting customer {}", id,resourceNotFoundException );
                            return resourceNotFoundException;
                        }
                );
    }

    public Customer getCustomerByEmail(String email) {
        // LOGGER.info("getCustomerByEmail started: email [{}]",email);
        log.info("getCustomerByEmail started: email [{}]",email);
        return customerDAO.getCustomerByEmail(email)
                .orElseThrow(
                        () -> {
                            ResourceNotFoundException resourceNotFoundException =
                                    new ResourceNotFoundException("customer with email [%s] is not found".formatted(email));
                            // LOGGER.error("getCustomerById(): error getting customer {}", email,resourceNotFoundException );
                            log.error("getCustomerById(): error getting customer {}", email,resourceNotFoundException );
                            return resourceNotFoundException;
                        }
                );
    }
    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // Check if the customer already exist.
        // email as unique key
        if (customerDAO.exitsCustomerWithEmail(customerRegistrationRequest.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email [%s] already exists"
                    .formatted(customerRegistrationRequest.getEmail()));
        }
        customerDAO.insertCustomer(
                new Customer(customerRegistrationRequest.getName(),
                        customerRegistrationRequest.getPassword(), customerRegistrationRequest.getEmail(),
                        customerRegistrationRequest.getAge())
        );
    }

    public void deleteCustomerById(Integer id) {
        // First check if the customer exits
        if (customerDAO.exitsCustomerWithId(id)) {
            customerDAO.deleteCustomerById(id);
        } else {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest updateRequest) {
        // Check if the id exists
        if (!customerDAO.exitsCustomerWithId(id)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
        // Check if there is changes
        Customer customer = getCustomerById(id);
        boolean somethingChange = false;
        // some of the fields are updated
        if (updateRequest.name() != null &&
                !customer.getName().equals(updateRequest.name())) {
            customer.setName(updateRequest.name());
            somethingChange = true;
        }
        if (updateRequest.password() != null &&
                !customer.getName().equals(updateRequest.password())) {
            customer.setName(updateRequest.password());
            somethingChange = true;
        }
        if (updateRequest.email() != null &&
                !customer.getEmail().equals(updateRequest.email())) {
            if (customerDAO.exitsCustomerWithEmail(updateRequest.email())) {
                throw new CustomerAlreadyExistsException("Customer with email [%s] already exists"
                        .formatted(updateRequest.email()));
            }
            customer.setEmail(updateRequest.email());
            somethingChange = true;
        }
        if (updateRequest.age() != null && !customer.getAge().equals(updateRequest.age())) {
            customer.setAge(updateRequest.age());
            somethingChange = true;
        }
        if (somethingChange) {
            customerDAO.updateCustomer(customer);
        } else {
            throw new RequestValidationException("No changes to update");
        }
    }
}
