package com.gcr.acm.common.logging;

import com.gcr.acm.common.aop.AopUtilities;
import com.gcr.acm.common.aop.MethodInvocationInfo;
import com.gcr.acm.common.utils.JsonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Aspect for logging.
 *
 * @author Razvan Dani
 */
@Aspect
@Order(2)
@Service
public class LoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger("jsonAuditLogger");

    @Autowired
    private LoggerService loggerService;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void beanAnnotatedWithService() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && beanAnnotatedWithService() && @annotation(Log)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        Date startTimestamp = new Date();
        Object returnedObject = null;
        Throwable throwable = null;

        try {
            returnedObject = joinPoint.proceed();
        } catch (Throwable t) {
            throwable = t;
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Log log = methodSignature.getMethod().getDeclaredAnnotation(Log.class);

        MethodInvocationInfo methodInvocationInfo = AopUtilities.getMethodInvocationInfo(joinPoint, startTimestamp,
                returnedObject, throwable, log.logParameters(), log.logReturnedObject());
        loggerService.saveAuditLogInfo(methodInvocationInfo);

        String methodInvocationInfoAsString = JsonUtils.getObjectMapper().writeValueAsString(methodInvocationInfo);
        methodInvocationInfoAsString = loggerService.replaceSensitiveData(methodInvocationInfoAsString);

        LOGGER.info("MethodInvocationInfo: " + methodInvocationInfoAsString);

        if (throwable != null) {
            throw throwable;
        }

        return returnedObject;
    }



    public static void initializeLogging() {
        String logAsJsonString = System.getProperty("logAsJson");

        if (logAsJsonString != null && logAsJsonString.equals("true")) {
//            java.net.URL url =   LoggerAspect.class.getClassLoader().getResource("logback-json.xml");
//            System.setProperty("logging.config", url.getPath());
            System.setProperty("logging.config", "classpath:logback-json.xml");
        }
    }
}
