package com.gcr.acm.common.utils;

import java.math.BigDecimal;

/**
 * Contains a count.
 *
 * @author Razvan Dani
 */
public class Counter {
	private BigDecimal count;

	public Counter(BigDecimal count) {
		this.count = count;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}
}
