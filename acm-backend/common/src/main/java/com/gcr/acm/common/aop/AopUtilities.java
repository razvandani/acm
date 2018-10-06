package com.gcr.acm.common.aop;

import com.gcr.acm.iam.user.UserIdentity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for AOP related operations.
 *
 * @author Razvan Dani
 */
public class AopUtilities {

    public static MethodInvocationInfo getMethodInvocationInfo(ProceedingJoinPoint joinPoint, Date startTimestamp, Object returnedObject, Throwable throwable,
                                                               boolean includeMethodParameters, boolean includeReturnedObject) {
        Date endTimestamp = new Date();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        MethodInvocationInfo methodInvocationInfo = new MethodInvocationInfo();
        methodInvocationInfo.setUserName(UserIdentity.getLoginUserName());
        methodInvocationInfo.setClassName(joinPoint.getTarget().getClass().getName());
        methodInvocationInfo.setMethodName(methodSignature.getMethod().getName());
        methodInvocationInfo.setStartTimestamp(startTimestamp);
        methodInvocationInfo.setEndTimestamp(endTimestamp);

        if (includeMethodParameters) {
            List<MethodInvocationInfo.ParameterInfo> parameterInfoList = new ArrayList<>();

            for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
                String parameterName = methodSignature.getParameterNames()[i];
                Object parameterValue = joinPoint.getArgs()[i];
                MethodInvocationInfo.ParameterInfo parameterInfo = new MethodInvocationInfo.ParameterInfo();
                parameterInfo.setParameterName(parameterName);
                parameterInfo.setParameterValue(parameterValue);

                parameterInfoList.add(parameterInfo);
            }

            methodInvocationInfo.setParameterInfoList(parameterInfoList);
        }

        if (includeReturnedObject) {
            methodInvocationInfo.setReturnedObject(returnedObject);
        }

        if (throwable != null) {
            methodInvocationInfo.setExceptionClassName(throwable.getClass().getName());
            methodInvocationInfo.setExceptionMessage(throwable.getMessage());
        }

        return methodInvocationInfo;
    }
}
