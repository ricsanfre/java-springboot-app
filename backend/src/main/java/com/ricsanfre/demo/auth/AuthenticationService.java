package com.ricsanfre.demo.auth;

import com.ricsanfre.demo.auth.AuthenticationRequest;
import com.ricsanfre.demo.auth.AuthenticationResponse;
import com.ricsanfre.demo.customer.Customer;
import com.ricsanfre.demo.customer.CustomerDTO;
import com.ricsanfre.demo.customer.CustomerDTOMapper;
import com.ricsanfre.demo.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            CustomerDTOMapper customerDTOMapper,
            JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        Customer principal = (Customer) authentication.getPrincipal();
        CustomerDTO customerDTO = customerDTOMapper.apply(principal);
        String token = jwtUtil.issueToken(customerDTO.userName(), customerDTO.userRoles());
        return new AuthenticationResponse(token, customerDTO);
    }
}
