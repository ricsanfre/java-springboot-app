package com.ricsanfre.demo.customer;

import com.ricsanfre.demo.exception.CustomerAlreadyExistsException;
import com.ricsanfre.demo.exception.RequestValidationException;
import com.ricsanfre.demo.exception.ResourceNotFoundException;
import com.ricsanfre.demo.s3.S3Buckets;
import com.ricsanfre.demo.s3.S3Service;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    // Logger SLF4J (Interface)
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    // DAO
    private final CustomerDAO customerDAO;

    private final CustomerDTOMapper customerDTOMapper;

    private final PasswordEncoder passwordEncoder;

    private final S3Service s3Service;

    private final S3Buckets s3Buckets;

    public CustomerService(
            @Qualifier("jpa") CustomerDAO customerDAO,
            CustomerDTOMapper customerDTOMapper,
            PasswordEncoder passwordEncoder, S3Service s3Service, S3Buckets s3Buckets) {
        this.customerDAO = customerDAO;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public List<CustomerDTO> getAllCustomers() {
        LOGGER.info("getAllCustomers() starting");
        return customerDAO.getAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer id) {
        LOGGER.info("getCustomerByID started: id [{}]", id);
        return customerDAO.getCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> {
                            ResourceNotFoundException resourceNotFoundException =
                                    new ResourceNotFoundException("customer with id [%s] is not found".formatted(id));
                            LOGGER.error("getCustomerById(): error getting customer {}", id, resourceNotFoundException);
                            return resourceNotFoundException;
                        }
                );
    }

    public CustomerDTO getCustomerByEmail(String email) {
        LOGGER.info("getCustomerByEmail started: email [{}]", email);
        return customerDAO.getCustomerByEmail(email)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> {
                            ResourceNotFoundException resourceNotFoundException =
                                    new ResourceNotFoundException("customer with email [%s] is not found".formatted(email));
                            LOGGER.error("getCustomerById(): error getting customer {}", email, resourceNotFoundException);
                            return resourceNotFoundException;
                        }
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

        // Check gender is valid value
        if (!EnumUtils.isValidEnum(Gender.class, customerRegistrationRequest.getGender())) {
            throw new RequestValidationException("Gender does not have a valid format. Permitted values: %s"
                    .formatted(Arrays.toString(Gender.values())));
        }

        // Check if the customer already exist.
        // email as unique key
        if (customerDAO.exitsCustomerWithEmail(customerRegistrationRequest.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email [%s] already exists"
                    .formatted(customerRegistrationRequest.getEmail()));
        }
        customerDAO.insertCustomer(
                new Customer(
                        customerRegistrationRequest.getName(),
                        passwordEncoder.encode(customerRegistrationRequest.getPassword()),
                        customerRegistrationRequest.getEmail(),
                        customerRegistrationRequest.getAge(),
                        Gender.valueOf(customerRegistrationRequest.getGender()))
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
        checkIfCustomerExists(id);

        // Get Customer from Id
        Customer customer = customerDAO.getCustomerById(id)
                .orElseThrow(
                        () -> {
                            ResourceNotFoundException resourceNotFoundException =
                                    new ResourceNotFoundException("customer with id [%s] is not found".formatted(id));
                            LOGGER.error("getCustomerById(): error getting customer {}", id, resourceNotFoundException);
                            return resourceNotFoundException;
                        }
                );

        // Check if there is changes
        boolean somethingChange = false;
        // some of the fields are updated
        if (updateRequest.name() != null &&
                !customer.getName().equals(updateRequest.name())) {
            customer.setName(updateRequest.name());
            somethingChange = true;
        }
        if (updateRequest.password() != null &&
                !customer.getPassword().equals(updateRequest.password())) {
            customer.setPassword(updateRequest.password());
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
        if (updateRequest.gender() != null) {
            // Check gender is valid value
            if (!EnumUtils.isValidEnum(Gender.class, updateRequest.gender())) {
                throw new RequestValidationException("Gender does not have a valid format. Permitted values: %s"
                        .formatted(Arrays.toString(Gender.values())));
            } else if (!customer.getGender().name().equals(updateRequest.gender())) {
                customer.setGender(Gender.valueOf(updateRequest.gender()));
                somethingChange = true;
            }
        }
        if (somethingChange) {
            customerDAO.updateCustomer(customer);
        } else {
            throw new RequestValidationException("No changes to update");
        }
    }

    private void checkIfCustomerExists(Integer id) {
        if (!customerDAO.exitsCustomerWithId(id)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
    }

    public void uploadCustomerProfileImage(Integer id, MultipartFile file) {

        // Check first if customer exists
        checkIfCustomerExists(id);

        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(id, profileImageId),
                    file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image", e);
        }

        // Store profileImageId in DB
        customerDAO.updateCustomerProfileImageId(id, profileImageId);

    }

    public byte[] getCustomerProfileImage(Integer id) {

        // Get Customer DTO from Id
        CustomerDTO customer = customerDAO.getCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> {
                            ResourceNotFoundException resourceNotFoundException =
                                    new ResourceNotFoundException("customer with id [%s] is not found".formatted(id));
                            LOGGER.error("getCustomerById(): error getting customer {}", id, resourceNotFoundException);
                            return resourceNotFoundException;
                        }
                );

        // Get profileImageId from customer
        String profileImageId = customer.profileImageId();

        if (StringUtils.isBlank(profileImageId)) {
            throw new ResourceNotFoundException("customer with id [%s] does not have a image profile".formatted(id));
        }
        return s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(id, profileImageId)
        );
    }
}
