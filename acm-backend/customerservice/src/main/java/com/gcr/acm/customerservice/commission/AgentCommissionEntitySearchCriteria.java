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
	private Integer contractType;
	private Integer commissionSubcategory;
	private Integer commissionSubcategoryElectricCurrent;

	public BigInteger getAgentId() {
		return agentId;
	}

	public void setAgentId(BigInteger agentId) {
		this.agentId = agentId;
	}

	public Integer getContractType() {
		return contractType;
	}

	public void setContractType(Integer contractType) {
		this.contractType = contractType;
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
