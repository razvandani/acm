package com.gcr.acm.customerservice.commission;

import com.gcr.acm.iam.user.UserEntity;
import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.*;
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
	@GeneratedValue
	private Integer id;

	@Column(name = "agent_id", insertable = false, updatable = false)
	private BigInteger agentId;

	@Column(name = "commission_type")
	private Integer commissionType;

	@Column(name ="commission_subcategory")
	private Integer commissionSubcategory;

	@Column(name ="commission_subcategory_start")
	private Integer commissionSubcategoryStart;

	@Column(name ="commission_subcategory_end")
	private Integer commissionSubcategoryEnd;

	@Column(name ="commission_value")
	private BigDecimal commissionValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agent_id", nullable = false)
	private UserEntity agentEntity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public UserEntity getAgentEntity() {
		return agentEntity;
	}

	public void setAgentEntity(UserEntity agentEntity) {
		this.agentEntity = agentEntity;
	}
}
