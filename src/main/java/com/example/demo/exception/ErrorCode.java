package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMPLOYEE_EXISTS(1001, "User already exists"),
    EMPLOYEE_NOT_FOUND(1002, "User not found"),
    UNAUTHORIZED_EXCEPTION(9999, "Unauthorized");

    private int code;
    private String message;
}
