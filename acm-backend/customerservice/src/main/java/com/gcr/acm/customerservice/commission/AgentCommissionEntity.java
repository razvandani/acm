package com.gcr.acm.customerservice.commission;

import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * JPA entity for agent_commission table.
 * 
 * @author Razvan Dani
 */
@Table(name = "agent_commission")
@Entity
public class AgentCommissionEntity extends EntityBase {
	@Column(name = "id")
	@Id
	private BigInteger id;

	@Column(name = "agent_id")
	private BigInteger agentId;

	@Column(name = "commission_type")
	private Integer commissionType;

	@Column(name ="commission_subcategory")
	private Integer commissionSubcategory;

	@Column(name ="commission_value")
	private BigDecimal commissionValue;

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

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

	public BigDecimal getCommissionValue() {
		return commissionValue;
	}

	public void setCommissionValue(BigDecimal commissionValue) {
		this.commissionValue = commissionValue;
	}
}
