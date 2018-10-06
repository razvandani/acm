package com.gcr.acm.restclient.exception;

import com.gcr.acm.restclient.ErrorWrapper;
import org.springframework.http.HttpStatus;

/**
 * @author Razvan Dani.
 */
public class UnauthorizedException extends RestException {

    public UnauthorizedException(Object responseObject) {
        super(HttpStatus.UNAUTHORIZED.value(), responseObject);
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), new ErrorWrapper(message, UnauthorizedException.class.getSimpleName()));
    }
}
