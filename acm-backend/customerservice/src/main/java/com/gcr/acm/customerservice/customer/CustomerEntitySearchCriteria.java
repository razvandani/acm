package com.gcr.acm.customerservice.customer;

import com.gcr.acm.jpaframework.EntitySearchCriteria;

import java.math.BigInteger;
import java.util.Date;

/**
 * Entity search criteria.
 *
 * @author Razvan Dani
 */
public class CustomerEntitySearchCriteria extends EntitySearchCriteria {
    private String firstNameStartsWith;
    private String lastNameStartsWith;
    private Integer countyId;
    private String locationStartsWith;
    private Boolean isActive;
    private BigInteger agentId;
    private Date contractStartDate;
    private Date contractEndDate;
    private Integer status;

    public String getFirstNameStartsWith() {
        return firstNameStartsWith;
    }

    public void setFirstNameStartsWith(String firstNameStartsWith) {
        this.firstNameStartsWith = firstNameStartsWith;
    }

    public String getLastNameStartsWith() {
        return lastNameStartsWith;
    }

    public void setLastNameStartsWith(String lastNameStartsWith) {
        this.lastNameStartsWith = lastNameStartsWith;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public String getLocationStartsWith() {
        return locationStartsWith;
    }

    public void setLocationStartsWith(String locationStartsWith) {
        this.locationStartsWith = locationStartsWith;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public BigInteger getAgentId() {
        return agentId;
    }

    public void setAgentId(BigInteger agentId) {
        this.agentId = agentId;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
