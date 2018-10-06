package com.gcr.acm.common.exception;

import javax.persistence.PersistenceException;
import javax.validation.ValidationException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Razvan Dani.
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {RestException.class})
    protected ResponseEntity<Object> handleRestException(RestException e, WebRequest request) {
        LOGGER.error("Exception handled in RestResponseEntityExceptionHandler", e);

        return handleExceptionInternal(e, e.getResponseObject(), new HttpHeaders(), e.getHttpStatus(), request);
    }

    @ExceptionHandler(value = {PersistenceException.class})
    protected ResponseEntity<Object> handlePersistenceException(PersistenceException e, WebRequest request) {
        LOGGER.error("ConstraintViolationException handled in RestResponseEntityExceptionHandler", e);
        String errorMessage = null;

        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause();
            String sqlErrorMesage = constraintViolationException.getSQLException().getMessage();

            if (sqlErrorMesage != null && sqlErrorMesage.contains("Duplicate")) {
                errorMessage = "duplicate entity";
            }
        }

        if (errorMessage == null) {
            errorMessage = e.getMessage();
        }

        return handleExceptionInternal(e, new ErrorWrapper(errorMessage, e.getClass().getSimpleName()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {ValidationException.class, JsonMappingException.class, HttpMessageNotReadableException.class })
    protected ResponseEntity<Object> handleValidationErrors(RuntimeException e, WebRequest request) {
        LOGGER.error("Exception handled in RestResponseEntityExceptionHandler", e);

        return handleExceptionInternal(e, new ErrorWrapper(e.getMessage(), e.getClass().getSimpleName()),
                new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleDefaultErrors(Exception e, WebRequest request) {
        LOGGER.error("Exception handled in RestResponseEntityExceptionHandler", e);

        return handleExceptionInternal(e, new ErrorWrapper(e.getMessage(), e.getClass().getSimpleName()),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * A single place to customize the response body of all Exception types.
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * body, headers, and status.
     * @param ex the exception
     * @param body the body for the response
     * @param headers the headers for the response
     * @param status the response status
     * @param request the current request
     */
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error("Exception handled in RestResponseEntityExceptionHandler", ex);

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
