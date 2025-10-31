package com.example.demo.exception;

import com.example.demo.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NotFoundException.class)
    ResponseEntity<ApiResponse<Object>> handlingNotFoundException(NotFoundException e) {
//        ApiResponse response = new ApiResponse(e.getCode(), e.getMessage(), Optional.empty());

//        response.setCode(e.getCode());
//        response.setMessage(e.getMessage());
//        response.setCode(ErrorCode.UNAUTHORIZED_EXCEPTION.getCode());
//        response.setMessage(ErrorCode.UNAUTHORIZED_EXCEPTION.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(
                        e.getCode(),
                        e.getMessage(),
                        null
                ));
    }
}
