package com.gcr.acm.customerservice.commission;

import com.gcr.acm.jpaframework.EntitySearchCriteria;

import java.math.BigInteger;

/**
 * Entity search criteria for agent commissions.
 *
 * @author Razvan Dani
 */
public class AgentCommissionEntitySearchCriteria extends EntitySearchCriteria {
	private BigInteger agentId;
	private Integer commissionType;
	private Integer commissionSubcategory;
	private Integer commissionSubcategoryElectricCurrent;

	public BigInteger getAgentId() {
		return agentId;
	}

	public void setAgentId(BigInteger agentId) {
		this.agentId = agentId;
	}

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

	public Integer getCommissionSubcategoryElectricCurrent() {
		return commissionSubcategoryElectricCurrent;
	}

	public void setCommissionSubcategoryElectricCurrent(Integer commissionSubcategoryElectricCurrent) {
		this.commissionSubcategoryElectricCurrent = commissionSubcategoryElectricCurrent;
	}
}
