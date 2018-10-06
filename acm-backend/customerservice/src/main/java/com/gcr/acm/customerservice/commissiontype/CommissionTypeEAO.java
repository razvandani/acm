package com.gcr.acm.customerservice.commissiontype;

import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import org.springframework.stereotype.Component;

/**
 * Entity access object for commissions.
 *
 * @author Razvan Dani
 */
@Component
public class CommissionTypeEAO extends EntityAccessObjectBase {

	public CommissionTypeEntity getCommissionType(Integer id) {
		return getEntity(CommissionTypeEntity.class, id);
	}
}
