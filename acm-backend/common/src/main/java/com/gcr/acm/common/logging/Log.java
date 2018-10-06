package com.gcr.acm.common.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method for logging.
 *
 * @author Razvan Dani
 */
@Target({ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME) public @interface Log {
    boolean logParameters() default true;
    boolean logReturnedObject() default true;
}
