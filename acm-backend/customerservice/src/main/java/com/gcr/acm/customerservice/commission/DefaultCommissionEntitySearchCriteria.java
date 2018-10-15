package com.gcr.acm.customerservice.commission;

import com.gcr.acm.jpaframework.EntitySearchCriteria;

/**
 * Entity search criteria for default commissions.
 *
 * @author Razvan Dani
 */
public class DefaultCommissionEntitySearchCriteria extends EntitySearchCriteria {
	private Integer commissionType;
	private Integer commissionSubcategory;

	public Integer getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(Integer commissionType) {
		this.commissionType = commissionType;
	}

	public Integer getCommissionSubcategory() {
		return commissionSubcategory;
	}

	public void setCommissionSubcategory(Integer commissionSubcategory) {
		this.commissionSubcategory = commissionSubcategory;
	}
}
