package com.gcr.acm.customerservice.customer;

public class SearchCustomerCriteria {
    private String firstNameStartsWith;
    private String lastNameStartsWith;
    private Integer countyId;
    private String locationStartsWith;
    private Boolean isActive;
    private Integer startIndex;
    private Integer pageSize;
    private String agentId;

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
}