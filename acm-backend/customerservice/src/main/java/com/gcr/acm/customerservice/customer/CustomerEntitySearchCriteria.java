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
    private Date startDate;
    private Date endDate;
    private Date deliveryStartDate;
    private Date deliveryEndDate;
    private Integer productType;
    private String firstNameStartsWith;
    private String lastNameStartsWith;
    private Integer countyId;
    private String locationStartsWith;
    private Boolean isActive;
    private BigInteger agentId;
    private Integer status;

    public Date getDeliveryStartDate() {
        return deliveryStartDate;
    }

    public void setDeliveryStartDate(Date deliveryStartDate) {
        this.deliveryStartDate = deliveryStartDate;
    }

    public Date getDeliveryEndDate() {
        return deliveryEndDate;
    }

    public void setDeliveryEndDate(Date deliveryEndDate) {
        this.deliveryEndDate = deliveryEndDate;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }
}
