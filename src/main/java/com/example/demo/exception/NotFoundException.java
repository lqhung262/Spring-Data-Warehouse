package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {
    //    private ErrorCode errorCode;
    private static final String ERROR_MESSAGE = "%s not found";
    private final int code = 400;


    public NotFoundException(String entityName) {
        super(String.format(ERROR_MESSAGE, entityName));
    }
}
