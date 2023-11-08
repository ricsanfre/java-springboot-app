package com.ricsanfre.demo.customer;

import com.ricsanfre.demo.exception.CustomerAlreadyExistsException;
import com.ricsanfre.demo.exception.RequestValidationException;
import com.ricsanfre.demo.exception.ResourceNotFoundException;
import com.ricsanfre.demo.s3.S3Buckets;
import com.ricsanfre.demo.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CustomerDAO customerDAO;
    @Mock
    private S3Service s3Service;

    @Mock
    private S3Buckets s3Buckets;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();
    // Not needed with @ExtendWith annotation
    // private AutoCloseable autoCloseable;
    @BeforeEach
    void setUp() {
        // Not needed with @ExtendWith annotation
        // autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(
                customerDAO,
                customerDTOMapper,
                passwordEncoder,
                s3Service,
                s3Buckets);
    }
// Not needed with @ExtendWith annotation
//    @AfterEach
//    void tearDown() throws Exception {
//        autoCloseable.close();
//    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();
        // Then
        Mockito.verify(customerDAO).getAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        // Given
        Integer id=1;
        Customer customer = new Customer("foo","123","foo@mail.com",18,Gender.FEMALE);
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);
        // When
        CustomerDTO actual = underTest.getCustomerById(id);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCustomerByIdThrowExceptionWhenIdNotExist() {
        // Given
        Integer id=1;
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(()-> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] is not found".formatted(id));
    }
    @Test
    void canGetCustomerByEmail() {
        // Given
        String email="foo@example.com";
        Customer customer = new Customer(1,"foo","123","foo@mail.com",18, Gender.FEMALE);
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.getCustomerByEmail(email)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomerByEmail(email);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void throwEmailNotFoundWhenGettingCustomerByEmail() {
        // Given
        String email="foo@example.com";
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.getCustomerByEmail(email)).thenReturn(Optional.empty());
        // When/Then
        assertThatThrownBy(() -> underTest.getCustomerByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with email [%s] is not found".formatted(email));
    }
    @Test
    void addCustomer() {
        // Given
        Integer id=1;
        String email = "foo@mail.com";
        String password="password";
        Customer customer = new Customer("foo",password, email,18, Gender.FEMALE);

        Mockito.when(customerDAO.exitsCustomerWithEmail(email)).thenReturn(false);

        String passwordHash = String.valueOf(password.hashCode());

        Mockito.when(passwordEncoder.encode(password)).thenReturn(passwordHash);

        // When
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest(
                        "foo",
                        password,
                        email,
                        21,
                        "FEMALE");

        underTest.addCustomer(request);
        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).insertCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(request.getName());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedCustomer.getAge()).isEqualTo(request.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }
    @Test
    void throwsExceptionWhenCustomerAlreadyExists() {
        // Given
        Integer id = 1;
        String email = "foo@mail.com";
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest(
                        "foo",
                        "123",
                        email,
                        21,
                        "FEMALE");

        Mockito.when(customerDAO.exitsCustomerWithEmail(email)).thenReturn(true);
        // When
        // Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("Customer with email [%s] already exists"
                        .formatted(request.getEmail()));
        Mockito.verify(customerDAO, Mockito.never()).insertCustomer(any());
    }
    @Test
    void deleteCustomerById() {
        // Given
        Integer id=1;
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        // When
        underTest.deleteCustomerById(id);
        // Then
        Mockito.verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void throwErrorWhenNonExistingIdWhenDeletingCustomer() {
        // Given
        Integer id=1;
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(false);
        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
        Mockito.verify(customerDAO, Mockito.never()).deleteCustomerById(id);
    }
    @Test
    void updateCustomerName() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest("foo", null, null, null, null);
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id,request);
        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }
    @Test
    void updateCustomerPassword() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, "foo@mail.com", null, null, null);
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id,request);
        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getPassword()).isEqualTo(request.password());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }
    @Test
    void updateCustomerAge() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, null, 25, null);
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id,request);
        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }
    @Test
    void updateCustomerEmail() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, "foo@mail.com", null, null);
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDAO.exitsCustomerWithEmail(request.email())).thenReturn(false);

        underTest.updateCustomer(id,request);
        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateCustomerGender() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, null, null, "MALE");
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        underTest.updateCustomer(id,request);
        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDAO).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getPassword()).isEqualTo(customer.getPassword());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(Gender.valueOf(request.gender()));
    }
    @Test
    void throwErrorWhenNotValidGenderWhenUpdatingCustomer() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, null, null, "FOO");
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Gender does not have a valid format. Permitted values: %s"
                        .formatted(Arrays.toString(Gender.values())));
        Mockito.verify(customerDAO, Mockito.never()).updateCustomer(customer);
    }
    @Test
    void updateCustomerEmailFailsWhenExistingEmail() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, "foo@mail.com", null, null);
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDAO.exitsCustomerWithEmail(request.email())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("Customer with email [%s] already exists"
                        .formatted(request.email()));
        Mockito.verify(customerDAO, Mockito.never()).updateCustomer(customer);
    }
    @Test
    void updateCustomerFailsWhenNotExistingId() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, "foo@mail.com", null, null);
        Customer customer = new Customer (id,"Jonh Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
        Mockito.verify(customerDAO, Mockito.never()).updateCustomer(customer);
    }
    @Test
    void updateCustomerWithoutChanges() {
        // Given
        Integer id=1;
        CustomerUpdateRequest request = new CustomerUpdateRequest("John Doe", "123", "john@mail.com", 22, "FEMALE");
        Customer customer = new Customer (id,"John Doe","123","john@mail.com", 22, Gender.FEMALE);

        // When
        // Configuring behaviour of the Mock
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);
        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No changes to update");
        Mockito.verify(customerDAO, Mockito.never()).updateCustomer(customer);
    }

    @Test
    void canUploadCustomerProfileImage() {
        // Given
        Integer id=1;

        byte[] file = "Hello World!".getBytes();
        MultipartFile multipartfile = new MockMultipartFile("fileName", file);

        String bucket = "customer-bucket";
        Mockito.when(s3Buckets.getCustomer()).thenReturn(bucket);
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.uploadCustomerProfileImage(id, multipartfile);

        // then
        ArgumentCaptor<String> profileImageIdCaptured = ArgumentCaptor.forClass(String.class);
        Mockito.verify(customerDAO).updateCustomerProfileImageId(
                eq(id),
                profileImageIdCaptured.capture());

        Mockito.verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(id, profileImageIdCaptured.getValue()),
                file);

    }

    @Test
    void cannotUploadCustomerProfileImageWhenUserDoesNotExists() {
        // Given
        Integer id=1;

        byte[] file = "Hello World!".getBytes();
        MultipartFile multipartfile = new MockMultipartFile("fileName", file);

        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(id, multipartfile))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        Mockito.verifyNoInteractions(s3Buckets);
        Mockito.verifyNoInteractions(s3Service);

        Mockito.verify(customerDAO, Mockito.never()).updateCustomerProfileImageId(eq(id), anyString());
    }

    @Test
    void throwExceptionWhenUploadCustomerProfileImage() throws IOException {
        // Given
        Integer id=1;

        byte[] file = "Hello World!".getBytes();
        MultipartFile multipartfile = Mockito.mock(MultipartFile.class);

        String bucket = "customer-bucket";

        Mockito.when(s3Buckets.getCustomer()).thenReturn(bucket);
        Mockito.when(multipartfile.getBytes()).thenThrow(IOException.class);
        Mockito.when(customerDAO.exitsCustomerWithId(id)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(id, multipartfile))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(IOException.class)
                .hasMessage("Failed to upload profile image");

        Mockito.verify(customerDAO, Mockito.never()).updateCustomerProfileImageId(eq(id), anyString());

    }

    @Test
    void canDownloadCustomerProfileImage() {

        // Given
        Integer id=1;
        String profileImageId = UUID.randomUUID().toString();
        Customer customer = new Customer(
                id,
                "John Doe",
                "123",
                "john@mail.com",
                22,
                Gender.FEMALE,
                profileImageId
        );
        byte[] imageContent = "ImageContent".getBytes();
        String bucket = "customer-bucket";

        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(s3Buckets.getCustomer()).thenReturn(bucket);
        Mockito.when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(id, profileImageId)))
                .thenReturn(imageContent);
        // When
        byte[] actual = underTest.getCustomerProfileImage(id);

        // Then
        assertThat(actual).isEqualTo(imageContent);
    }

    @Test
    void cannotDownloadCustomerProfileImageWhenCustomerDoesNotExits() {

        // Given
        Integer id=1;

        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] is not found".formatted(id));

        // Then
        Mockito.verifyNoInteractions(s3Service);
        Mockito.verifyNoInteractions(s3Buckets);

    }

    @Test
    void cannotDownloadCustomerProfileImageWhenProfileImageDoesNotExits() {

        // Given
        Integer id=1;
        Customer customer = new Customer(
                id,
                "John Doe",
                "123",
                "john@mail.com",
                22,
                Gender.FEMALE
        );
        byte[] imageContent = "ImageContent".getBytes();
        String bucket = "customer-bucket";

        Mockito.when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        assertThatThrownBy(() -> underTest.getCustomerProfileImage(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] does not have a image profile".formatted(id));

        // Then
        Mockito.verifyNoInteractions(s3Service);
        Mockito.verifyNoInteractions(s3Buckets);

    }
}