package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyExistsException extends RuntimeException {
    private static final String ERROR_MESSAGE = "%s already exists.";
    private final int code = 400;

    public AlreadyExistsException(String entityName) {
        super(String.format(ERROR_MESSAGE, entityName));
    }
}
