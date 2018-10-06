package com.gcr.acm.restclient.exception;

import com.gcr.acm.restclient.ErrorWrapper;
import org.springframework.http.HttpStatus;

/**
 * @author Razvan Dani.
 */
public class NotFoundException extends RestException {

    public NotFoundException(Object responseObject) {
        super(HttpStatus.NOT_FOUND.value(), responseObject);
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND.value(), new ErrorWrapper(message, NotFoundException.class.getSimpleName()));
    }
}
