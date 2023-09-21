package com.ricsanfre.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // @RequestMapping(path = "api/v1/customer", method = RequestMethod.GET)
    @GetMapping("api/v1/customer")
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }
    @GetMapping("api/v1/customer/{id}")
    public Customer getCustomer(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }
    @PostMapping(value = "api/v1/customer")
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);

    }
    @DeleteMapping("api/v1/customer/{id}")
    public void deleteCustomer(@PathVariable("id") Integer id) {
        customerService.deleteCustomerById(id);

    }
}
