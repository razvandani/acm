package com.gcr.acm.methodcache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be placed on any method for which method caching needs to be applied. The restriction is tha the
 * class in which the method is defined, does not hold state variables that change during time in such way that it
 * would influence the result of method executions in subsequent runs, because in such cases method caching may
 * produce extremly unpredictable/undesirable results.
 */
@Target({ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME) public @interface MethodCache {
    // cache is enabled (1 or true in Parameters means enabled)
    boolean cacheDependsOnLoginUser() default false;  // if set to true, cache entries will store the login user name
    // that invoked the method and only subsequent identical methods
    // (same method params) from the SAME user will benefit from the cache entry.
    // If this is set to false, method caches are reused among any users
    String[] resultObjectCacheKeywords();               // list of attribute names of the result result objects, or if the result
    // object is any Collection, the this represents list of attribute names
    // from the objects contained in the Collection. The usage of the keywords
    // is for being able to evict relevant method cache manually, when a certain
    // keyword has its related data changed in a manner than makes the cache obsolete.
    // A keyword can also represent a hierarcy of attributes, e.g. x.y means attribute y
    // of the attribute x of the result object

    int expirationSeconds() default 0; // if 0, it doesn't expire

    int localMemoryExpirationSeconds() default 0; // if 0, no local memory cache is used
}
