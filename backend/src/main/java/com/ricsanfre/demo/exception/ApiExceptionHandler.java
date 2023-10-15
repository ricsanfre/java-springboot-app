package com.ricsanfre.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = RequestValidationException.class)
    public ResponseEntity<Object> handleRequestValidationException(RequestValidationException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = CustomerAlreadyExistsException.class)
    public ResponseEntity<Object> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                e.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
    }
}
