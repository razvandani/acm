package com.gcr.acm.customerservice.commissiontype;

import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * JPA entity for commission_type table.
 *
 * @author Razvan Dani
 */
@Table(name = "commission_type")
@Entity
public class CommissionTypeEntity extends EntityBase {
	@Column(name = "id")
	@Id
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "commission_value")
	private BigDecimal commissionValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getCommissionValue() {
		return commissionValue;
	}

	public void setCommissionValue(BigDecimal commissionValue) {
		this.commissionValue = commissionValue;
	}
}
