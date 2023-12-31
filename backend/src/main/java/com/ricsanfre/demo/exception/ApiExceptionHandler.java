package com.ricsanfre.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException e,
            HttpServletRequest request,
            HttpServletResponse response) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RequestValidationException.class)
    public ResponseEntity<Object> handleRequestValidationException(
            RequestValidationException e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CustomerAlreadyExistsException.class)
    public ResponseEntity<Object> handleCustomerAlreadyExistsException(
            CustomerAlreadyExistsException e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InsufficientAuthenticationException.class)
    public ResponseEntity<Object> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Object> handleInsufficientBadCredentialsException(
            BadCredentialsException e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = SdkServiceException.class)
    public ResponseEntity<Object> handleAWSSdkServiceException(
            SdkServiceException e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SdkClientException.class)
    public ResponseEntity<Object> handleAWSSdkClientException(
            SdkClientException e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(
            Exception e,
            HttpServletRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
