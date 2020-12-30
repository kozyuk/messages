package com.skf.messages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class LastNotFoundException extends RuntimeException {
    public LastNotFoundException(String message) {
        super(message);
    }
}
