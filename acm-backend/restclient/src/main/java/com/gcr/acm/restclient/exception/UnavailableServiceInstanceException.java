package com.gcr.acm.restclient.exception;

import com.gcr.acm.restclient.ErrorWrapper;
import org.springframework.http.HttpStatus;

/**
 * @author Razvan Dani.
 */
public class UnavailableServiceInstanceException extends RestException {

    public UnavailableServiceInstanceException(Object responseObject) {
        super(HttpStatus.SERVICE_UNAVAILABLE.value(), responseObject);
    }

    public UnavailableServiceInstanceException(String serviceName) {
        super(HttpStatus.SERVICE_UNAVAILABLE.value(), new ErrorWrapper("Service Unavailable: " + serviceName,
                UnavailableServiceInstanceException.class.getSimpleName()));
    }
}
