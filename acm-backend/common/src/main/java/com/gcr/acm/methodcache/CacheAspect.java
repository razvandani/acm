package com.gcr.acm.methodcache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Aspect for intercepting methods annotated with @MethodCache.
 *
 * @author Razvan Dani
 */
@Component
@Aspect
public class CacheAspect {
    @Autowired
    private CacheComponent methodCacheComponent;

    @Around("@annotation(MethodCache) && execution(* *(..))")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnedObject;

        ResultObjectContainer resultObjectContainer = methodCacheComponent.getResultFromCache(joinPoint);

        if (resultObjectContainer == null) {
            returnedObject = joinPoint.proceed();
            methodCacheComponent.addMethodCallToCache(joinPoint, returnedObject);
        } else {
            returnedObject = resultObjectContainer.getResultObject();
        }

        return returnedObject;
    }

//    @Value("${cache.enabled}")
//    private Boolean cacheEnabled;
//
//    @Around("@annotation(MethodCache) && execution(* *(..))")
//    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object returnedObject;
//
//        if (cacheEnabled) {
//            ResultObjectContainer resultObjectContainer = methodCacheComponent.getResultFromCache(joinPoint);
//
//            if (resultObjectContainer == null) {
//                returnedObject = joinPoint.proceed();
//                methodCacheComponent.addMethodCallToCache(joinPoint, returnedObject);
//            } else {
//                returnedObject = resultObjectContainer.getResultObject();
//            }
//        } else {
//            returnedObject = joinPoint.proceed();
//        }
//
//        return returnedObject;
//    }
}
