package com.gcr.acm.customerservice.customer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gcr.acm.common.utils.JsonDateSerializer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Contains information about a customer.
 *
 * @author Razvan Dani
 */
public class CustomerInfo  {
    public static final Integer STATUS_NOT_DELIVERED_TO_ADMIN = 1;
    public static final Integer STATUS_DELIVERED_TO_ADMIN = 2;

    private String id;
    private String contractNumber;
    private String firstName;
    private String lastName;
    private Integer countyId;
    private String location;
    private String street;
    private String streetNumber;
    private String phoneNumber;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date contractDate;
    private Boolean isActive;
    private String agentId;
    // todo agent name?
    private String countyName; // readonly
    private Integer productType; // 1 = Electric energy, 2 = Natural Gas
    private Integer contractType; // 1 = Fix, 2 = E-go, 3 = Flex, 4 = Flux
    private Integer commissionType; // 1 = B1, 2 = B2, 3 = B3, 4 = B4
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date startDeliveryDate; // date when the service (energy or gas) starts
    private Integer status; // 1 = Not Delivered to Admin, 2 = Delivered to Admin
    private BigDecimal commission;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Integer getContractType() {
        return contractType;
    }

    public void setContractType(Integer contractType) {
        this.contractType = contractType;
    }

    public Integer getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Integer commissionType) {
        this.commissionType = commissionType;
    }

    public Date getStartDeliveryDate() {
        return startDeliveryDate;
    }

    public void setStartDeliveryDate(Date startDeliveryDate) {
        this.startDeliveryDate = startDeliveryDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}
