package com.gcr.acm.restclient.exception;

import com.gcr.acm.restclient.ErrorWrapper;
import org.springframework.http.HttpStatus;

/**
 * @author Razvan Dani.
 */
public class ForbiddenException extends RestException {

    public ForbiddenException(Object responseObject) {
        super(HttpStatus.FORBIDDEN.value(), responseObject);
    }

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN.value(), new ErrorWrapper(message, ForbiddenException.class.getSimpleName()));
    }

}
