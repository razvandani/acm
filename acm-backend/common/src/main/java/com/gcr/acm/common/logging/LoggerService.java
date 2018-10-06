package com.gcr.acm.common.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gcr.acm.common.aop.MethodInvocationInfo;
import com.gcr.acm.common.utils.JsonUtils;
import com.gcr.acm.common.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoggerService {
    private static final Logger LOGGER = LoggerFactory.getLogger("jsonAuditLogger");
    private static final String SENSITIVE_DATA_REGEX = "[\\p{Punct}\\s]*)(.*?)(\"[,\"\\s]+.*$)";

    @Autowired
    private AuditLogEAO auditLogEAO;
    /**
     * Saves audit log info.
     *
     * @param methodInvocationInfo The methodInvocationInfo
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAuditLogInfo(MethodInvocationInfo methodInvocationInfo) throws JsonProcessingException {
        try {
            AuditLogEntity auditLogEntity = new AuditLogEntity();
            auditLogEntity.setClassName(methodInvocationInfo.getClassName());
            auditLogEntity.setMethodName(methodInvocationInfo.getMethodName());
            auditLogEntity.setUserName(methodInvocationInfo.getUserName());
            auditLogEntity.setStartTimestamp(methodInvocationInfo.getStartTimestamp());
            auditLogEntity.setEndTimestamp(methodInvocationInfo.getEndTimestamp());
            auditLogEntity.setExceptionMessage(methodInvocationInfo.getExceptionMessage());

            auditLogEntity.setParameterInfoListJson(replaceSensitiveData(JsonUtils.getObjectMapper().writeValueAsString(methodInvocationInfo.getParameterInfoList())));
            auditLogEntity.setReturnedObjectJson(replaceSensitiveData(JsonUtils.getObjectMapper().writeValueAsString(methodInvocationInfo.getReturnedObject())));

            auditLogEAO.saveAuditLog(auditLogEntity);
        } catch (Throwable t) {
            LOGGER.error("Exception in saveAuditLogInfo", t);
        }
    }

    public String replaceSensitiveData(String sensitiveData) {
        if (!Utilities.isEmptyOrNull(sensitiveData)) {
            sensitiveData = sensitiveData.replaceAll("(.*password" + SENSITIVE_DATA_REGEX, "$1*****$3");
            sensitiveData = sensitiveData.replaceAll("(.*tempPassword"+ SENSITIVE_DATA_REGEX, "$1*****$3");
            sensitiveData = sensitiveData.replaceAll("(.*verificationId"+ SENSITIVE_DATA_REGEX, "$1*****$3");

        }

        return sensitiveData;
    }


}
