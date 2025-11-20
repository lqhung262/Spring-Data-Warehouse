package com.example.demo.exception;

import com.example.demo.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NotFoundException.class)
    ResponseEntity<ApiResponse<Object>> handlingNotFoundException(NotFoundException e) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(
                        e.getCode(),
                        e.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    ResponseEntity<ApiResponse<Object>> handlingAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        e.getCode(),
                        e.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    ResponseEntity<ApiResponse<Object>> handlingIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        400,
                        e.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiResponse<Object>> handlingResponseStatusException(ResponseStatusException e) {
        int code = e.getStatusCode().value();
        String msg = e.getReason() == null ? e.getMessage() : e.getReason();
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ApiResponse<>(
                        code,
                        msg,
                        null
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Object>> handlingDataIntegrity(DataIntegrityViolationException e) {
        // likely unique constraint violation in DB
        // getMostSpecificCause() is expected to be present for JDBC exceptions
        String msg = e.getMostSpecificCause().getMessage();
        return ResponseEntity
                .status(409)
                .body(new ApiResponse<>(409, msg, null));
    }
}
