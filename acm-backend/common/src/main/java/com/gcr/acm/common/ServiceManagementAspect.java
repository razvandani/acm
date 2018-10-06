package com.gcr.acm.common;

import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * Aspect called around the execution of any methods configured to be intercepted, in order to automatically handle JPA EntityManager management:
 * automatic creation of EntityManager before method execution, closing of EntityManager after execution, putting EntityManager in a ThreadLocal variable
 * inside EntityAccessObjectBase. So basically in order to use the methods offered by EntityAccessObjectBase, the methods must be intercepted by this Aspect.
 *
 * It also logs, if enabled, the duration in milliseconds of the execution of the public service methods.
 *
 * @author Razvan Dani
 */
@Component
@Aspect
@Order(1)
@PropertySource("classpath:common-${spring.profiles.active}.properties")
public class ServiceManagementAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManagementAspect.class);

    @Autowired
    @Qualifier("entityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Value("${logperformance.enabled}")
    private Boolean logPerformanceEnabled;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void beanAnnotatedWithService() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && beanAnnotatedWithService()")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Long start = System.currentTimeMillis();

        Object returnedObject = null;
        EntityManager previousEntityManager = EntityAccessObjectBase.getEntityManager();

        // create a container managed EntityManager (that has automatic transaction join and it's automatically closed)
        // and set is as available for the current thread so that EAO's can use it
        if (previousEntityManager == null) {
//            EntityManager entityManager = ExtendedEntityManagerCreator.createContainerManagedEntityManager(entityManagerFactory);
            EntityAccessObjectBase.setEntityManagerForThread(entityManager);
        }

        try {
            // execute intercepted method
            returnedObject = proceedingJoinPoint.proceed();
        } finally {
            if (previousEntityManager == null) {
                EntityAccessObjectBase.setEntityManagerForThread(null);
            }

            Long end = System.currentTimeMillis();

            if (logPerformanceEnabled) {
                MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
                DisablePerformanceMetrics disablePerformanceMetrics = methodSignature.getMethod().getDeclaredAnnotation(DisablePerformanceMetrics.class);

                if (disablePerformanceMetrics == null) {
                    LOGGER.info("Performance: " + proceedingJoinPoint.getTarget().getClass().getSimpleName() + "."
                            + ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod().getName() + " executed in " +
                            (end - start) + " millis");
                }
            }
        }

        return returnedObject;
    }
}
