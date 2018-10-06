package com.gcr.acm.common.aop;

import com.gcr.acm.common.utils.DateUtilities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Contains information about a method invocation.
 */
public class MethodInvocationInfo {
    private String userName;
    private String className;
    private String methodName;
    private Date startTimestamp;
    private Date endTimestamp;
    private List<ParameterInfo> parameterInfoList;
    private Object returnedObject;
    private String exceptionClassName;
    private String exceptionMessage;
    private String auditContent;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuditContent() {
        return auditContent;
    }

    public void setAuditContent(String auditContent) {
        this.auditContent = auditContent;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Date endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public List<ParameterInfo> getParameterInfoList() {
        return parameterInfoList;
    }

    public void setParameterInfoList(List<ParameterInfo> parameterInfoList) {
        this.parameterInfoList = parameterInfoList;
    }

    public Object getReturnedObject() {
        return returnedObject;
    }

    public void setReturnedObject(Object returnedObject) {
        this.returnedObject = returnedObject;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public static class ParameterInfo implements Serializable {
        private String parameterName;
        private Object parameterValue;

        public String getParameterName() {
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public Object getParameterValue() {
            return parameterValue;
        }

        public void setParameterValue(Object parameterValue) {
            this.parameterValue = parameterValue;
        }
    }

    public String getFormattedStartTimestamp() {
        return DateUtilities.formatDate(startTimestamp, DateUtilities.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILISENCOND_PATTERN);
    }

    public String getFormattedEndTimestamp() {
        return DateUtilities.formatDate(endTimestamp, DateUtilities.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILISENCOND_PATTERN);
    }
}
