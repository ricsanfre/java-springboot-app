package com.ricsanfre.demo.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ApiErrorResponse(
        String message,
        HttpStatus httpStatus,
        ZonedDateTime timestamp) {

}