package com.gcr.acm.customerservice.commission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gcr.acm.common.utils.JsonDateDeserializer;
import com.gcr.acm.common.utils.JsonDateSerializer;

import java.util.Date;

public class SearchCommissionCriteria {

	@JsonSerialize(using = JsonDateSerializer.class)
	private Date startDate;
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date endDate;
	private String agentId;

	public Date getStartDate() {
		return startDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
}
