package com.ricsanfre.demo.customer;

import com.ricsanfre.demo.jwt.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
public class CustomerController {

    private final CustomerService customerService;

    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    // @RequestMapping(path = "api/v1/customer", method = RequestMethod.GET)
    @GetMapping("api/v1/customer")
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }
    @GetMapping("api/v1/customer/{id}")
    public CustomerDTO getCustomer(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }
    @GetMapping("api/v1/findCustomer")
    public CustomerDTO getCustomerByEmail(@RequestParam(value = "email") String email) {
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("api/v1/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody @Valid CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        // Adding JWT Token to response headers
        String jwtToken = jwtUtil.issueToken(request.getEmail(), "ROLE_USER");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }
    @DeleteMapping("api/v1/customer/{id}")
    public void deleteCustomer(@PathVariable("id") Integer id) {
        customerService.deleteCustomerById(id);

    }
    @PutMapping("api/v1/customer/{id}")
    public void updateCustomer(@PathVariable("id") Integer id, @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomer(id,request);

    }

    @PostMapping(
            value = "api/v1/customer/{id}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCustomerProfileImage(
            @PathVariable("id") Integer id,
            @RequestParam("file") MultipartFile file) {

        customerService.uploadCustomerProfileImage(id,file);

    }
    @GetMapping(
            value = "api/v1/customer/{id}/profile-image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getCustomerProfileImage(
            @PathVariable("id") Integer id) {

        return customerService.getCustomerProfileImage(id);

    }
}
