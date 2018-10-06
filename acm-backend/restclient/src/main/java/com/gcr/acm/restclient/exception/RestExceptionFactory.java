package com.gcr.acm.restclient.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Razvan Dani.
 */
public class RestExceptionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionFactory.class);

    private static Map<Integer, Class<? extends RestException>> map = new HashMap<>();

    static {
        map.put(HttpStatus.NOT_FOUND.value(), NotFoundException.class);
        map.put(HttpStatus.INTERNAL_SERVER_ERROR.value(), ServerException.class);
        map.put(HttpStatus.UNAUTHORIZED.value(), UnauthorizedException.class);
        map.put(HttpStatus.FORBIDDEN.value(), ForbiddenException.class);
        map.put(HttpStatus.SERVICE_UNAVAILABLE.value(), UnavailableServiceInstanceException.class);
    }

    public static RestException getException(Integer status, Object object) {
        try {
            Class<? extends RestException> exceptionClass = map.get(status);

            if (exceptionClass != null) {
                Constructor<? extends RestException> constructor = exceptionClass.getConstructor(Object.class);

                if (constructor != null) {
                    return constructor.newInstance(object);
                }
            }

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                InvocationTargetException ignored) {
            LOGGER.warn("Cannot instantiate exception", ignored);
            //do nothing return default rest exception
        }
        return new RestException(status, object);
    }
}
