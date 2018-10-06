package com.gcr.acm.jpaframework;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class AspectUtilities {
    private static final String GETTER_METHOD_PREFIX = "get";
    private static final String COMPANY_ID_FIELD_NAME = "customerId";
    private static final String TENANT_ID_FIELD_NAME = "tenantId";

    private static ConcurrentHashMap<String, MethodContainer> methodContainerByClassNameAndFieldNameMap = new ConcurrentHashMap<>();

    public static Integer getCompanyIdFromJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        return (Integer) getFieldFromJoinPoint(proceedingJoinPoint, COMPANY_ID_FIELD_NAME);
    }

    public static Integer getTenantIdFromJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        return (Integer) getFieldFromJoinPoint(proceedingJoinPoint, TENANT_ID_FIELD_NAME);
    }

    private static Object getFieldFromJoinPoint(ProceedingJoinPoint proceedingJoinPoint, String fieldName) {
        Object fieldValue = null;
        String[] parameterNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        int currentMethodParamIndex = 0;
        String getterMethodName = GETTER_METHOD_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        for (Object methodParam : proceedingJoinPoint.getArgs()) {
            if (methodParam != null) {
                String parameterName = parameterNames[currentMethodParamIndex];

                if (parameterName.equals(fieldName)) {
                    fieldValue = methodParam;
                } else {
                    Class methodParamClass = methodParam.getClass();
                    String methodParamClassName = methodParamClass.getName();
                    MethodContainer methodContainer =
                            methodContainerByClassNameAndFieldNameMap.get(methodParamClassName + "_" + fieldName);

                    if (methodContainer == null) {
                        Method method = null;
                        try {
                            method = methodParam.getClass().getMethod(getterMethodName);
                            method.setAccessible(true);
                        } catch (NoSuchMethodException ignore) {
                        }

                        methodContainer = new MethodContainer(method);
                        methodContainerByClassNameAndFieldNameMap.put(methodParamClassName + "_" + fieldName, methodContainer);
                    }

                    Method method = methodContainer.getMethod();

                    if (method != null) {
                        try {
                            fieldValue = method.invoke(methodParam);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            currentMethodParamIndex++;
        }

        return fieldValue;
    }

    private static class MethodContainer {
        private Method method;

        private MethodContainer(Method field) {
            this.method = field;
        }

        private Method getMethod() {
            return method;
        }
    }
}
