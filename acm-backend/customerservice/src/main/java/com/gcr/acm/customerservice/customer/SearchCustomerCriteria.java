package com.gcr.acm.customerservice.customer;

import java.util.Date;

public class SearchCustomerCriteria {
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
    private Integer startIndex;
    private Integer pageSize;
    private String agentId;
    private Integer status;
    private String contractNumber;
    private String street;

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

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}