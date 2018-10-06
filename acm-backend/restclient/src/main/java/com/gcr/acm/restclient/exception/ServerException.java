package com.gcr.acm.restclient.exception;

import com.gcr.acm.restclient.ErrorWrapper;
import org.springframework.http.HttpStatus;

/**
 * @author Razvan Dani.
 */
public class ServerException extends RestException {

    public ServerException(Object responseObject) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseObject);
    }

    public ServerException(Object responseObject, Exception e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseObject, e);
    }

    public ServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), new ErrorWrapper(message, ServerException.class.getSimpleName()));
    }

    public ServerException(String message, Exception e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), new ErrorWrapper(message, ServerException.class.getSimpleName()), e);
    }
}
