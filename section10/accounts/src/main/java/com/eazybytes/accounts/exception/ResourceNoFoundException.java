package com.eazybytes.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND )
public class ResourceNoFoundException extends RuntimeException {

    public ResourceNoFoundException(String resourceName, String fieldName, String fieldValue) {
        super("%s not found with the given input data %s: '%s'".formatted(resourceName, fieldName, fieldValue));
    }
}
