package com.ricsanfre.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
// @ResponseStatus(value = HttpStatus.CONFLICT)
// ApiException Handler is managing responses to all exceptions
public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
