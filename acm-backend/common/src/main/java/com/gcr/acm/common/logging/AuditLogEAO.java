package com.gcr.acm.common.logging;

import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import org.springframework.stereotype.Component;

/**
 * Entity access object for audit logs.
 *
 * @author Razvan Dani
 */
@Component
public class AuditLogEAO extends EntityAccessObjectBase {

    /**
     * Saves the auditLog entity.
     *
     * @param auditLogEntity    The AuditLogEntity
     * @return              The stored AuditLogEntity
     */
    public AuditLogEntity saveAuditLog(AuditLogEntity auditLogEntity) {
        return storeEntity(auditLogEntity);
    }

    /**
     * Gets the AuditLogEntity for the specified auditLog id.
     *
     * @param auditLogId    The auditLog id
     * @return          The AuditLogEntity
     */
    public AuditLogEntity getAuditLog(Integer auditLogId) {
        return getEntity(AuditLogEntity.class, auditLogId);
    }
}
