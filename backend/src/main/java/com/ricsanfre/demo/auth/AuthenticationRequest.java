package com.ricsanfre.demo.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
