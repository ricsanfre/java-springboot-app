package com.ricsanfre.demo.auth;

import com.ricsanfre.demo.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
