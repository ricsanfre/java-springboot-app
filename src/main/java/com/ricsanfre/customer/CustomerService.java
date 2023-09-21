package com.ricsanfre.customer;

import com.ricsanfre.exception.CustomerAlreadyExistsException;
import com.ricsanfre.exception.RequestValidationException;
import com.ricsanfre.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        return customerDAO.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] is not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // Check if the customer already exist.
        // email as unique key
        if (customerDAO.exitsCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new CustomerAlreadyExistsException("Customer with email [%s] already exists"
                    .formatted(customerRegistrationRequest.email()));
        }
        customerDAO.insertCustomer(
                new Customer(customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        customerRegistrationRequest.age())
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
