package com.gcr.acm.restclient.exception;

import org.springframework.http.HttpStatus;

/**
 * Encapsulates the details of a REST error response.
 *
 * @author Razvan Dani
 */
public class RestException extends RuntimeException {
    private Integer statusCode;
    private Object responseObject;

    public RestException(Integer statusCode, Object responseObject) {
        this.statusCode = statusCode;
        this.responseObject = responseObject;
    }

    public RestException(Integer statusCode, Object responseObject, Exception e) {
        super(e);
        this.statusCode = statusCode;
        this.responseObject = responseObject;
    }

    @Override
    public String getMessage() {
        return "Status code " + statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(statusCode);
    }

    public Object getResponseObject() {
        return responseObject;
    }
}
