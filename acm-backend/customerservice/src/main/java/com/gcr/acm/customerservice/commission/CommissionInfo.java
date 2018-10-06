package com.gcr.acm.customerservice.commission;

import java.math.BigDecimal;

/**
 * Contains information about a commission.
 *
 * @author Razvan Dani
 */
public class CommissionInfo {
	private BigDecimal commissionTotal;

	public BigDecimal getCommissionTotal() {
		return commissionTotal;
	}

	public void setCommissionTotal(BigDecimal commissionTotal) {
		this.commissionTotal = commissionTotal;
	}
}
