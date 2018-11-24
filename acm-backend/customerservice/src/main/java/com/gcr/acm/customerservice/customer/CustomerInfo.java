package com.gcr.acm.customerservice.customer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gcr.acm.common.utils.JsonDateSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Contains information about a customer.
 *
 * @author Razvan Dani
 */
public class CustomerInfo  {
    public static final Integer STATUS_NOT_DELIVERED_TO_ADMIN = 1;
    public static final Integer STATUS_DELIVERED_TO_ADMIN = 2;

    public static final Integer PRODUCT_TYPE_ELECTRIC_ENERGY = 1;
    public static final Integer PRODUCT_TYPE_NATURAL_GAS = 2;

    public static final Integer COMMISSION_TYPE_FIX = 1;
    public static final Integer COMMISSION_TYPE_EGO = 2;
    public static final Integer COMMISSION_TYPE_FLEX = 3;
    public static final Integer COMMISSION_TYPE_FLUX = 4;

    private String id;
    private String contractNumber;
    private String firstName;
    private String lastName;
    private Integer countyId;
    private String location;
    private String street;
    private String streetNumber;
    private String flat;
    private String stairNumber;
    private String apartmentNumber;
    private String phoneNumber;
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date contractDate;
    private Boolean isActive;
    private String agentId;
    private String agentName; // readonly
    private String countyName; // readonly
    private Integer productType; // 1 = Electric energy, 2 = Natural Gas
    private Integer commissionType; // 1 = Fix, 2 = E-go, 3 = Flex, 4 = Flux
    private Integer commissionSubcategory; // 1 = B1, 2 = B2, 3 = B3, 4 = B4
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

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getStairNumber() {
        return stairNumber;
    }

    public void setStairNumber(String stairNumber) {
        this.stairNumber = stairNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }
}
