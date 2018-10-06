package com.gcr.acm.common.logging;

import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

/**
 * JPA entity for audit_log table.
 *
 * @author Razvan Dani
 */
@Table(name = "audit_log")
@Entity
public class AuditLogEntity extends EntityBase {
    @Column(name = "id")
    @Id
    private BigInteger id;

    @Column(name ="class_name")
    private String className;

    @Column(name ="method_name")
    private String methodName;

    @Column(name ="user_name")
    private String userName;

    @Column(name ="start_timestamp")
    private Date startTimestamp;

    @Column(name ="end_timestamp")
    private Date endTimestamp;

    @Column(name = "parameter_info_list_json")
    private String parameterInfoListJson;

    @Column(name = "returned_object_json")
    private String returnedObjectJson;

    @Column(name = "exception_message")
    private String exceptionMessage;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getParameterInfoListJson() {
        return parameterInfoListJson;
    }

    public void setParameterInfoListJson(String parameterInfoListJson) {
        this.parameterInfoListJson = parameterInfoListJson;
    }

    public String getReturnedObjectJson() {
        return returnedObjectJson;
    }

    public void setReturnedObjectJson(String returnedObjectJson) {
        this.returnedObjectJson = returnedObjectJson;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
