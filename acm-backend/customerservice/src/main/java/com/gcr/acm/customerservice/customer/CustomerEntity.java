package com.gcr.acm.customerservice.customer;

import com.gcr.acm.customerservice.county.CountyEntity;
import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * JPA entity for customer table.
 *
 * @author Razvan Dani
 */
@Table(name = "customer")
@Entity
public class CustomerEntity extends EntityBase {
    @Column(name = "id")
    @Id
    private BigInteger id;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name ="first_name")
    private String firstName;

    @Column(name ="last_name")
    private String lastName;

    @Column(name ="county_id")
    private Integer countyId;

    @Column(name ="location")
    private String location;

    @Column(name ="street")
    private String street;

    @Column(name ="street_number")
    private String streetNumber;

    @Column(name ="flat")
    private String flat;

    @Column(name ="stair_number")
    private String stairNumber;

    @Column(name ="apartment_number")
    private String apartmentNumber;

    @Column(name ="phone_number")
    private String phoneNumber;

    @Column(name ="contract_date")
    private Date contractDate;

    @Column(name ="is_active")
    private Boolean isActive;

    @Column(name ="agent_id")
    private BigInteger agentId;

    @Column(name ="product_type")
    private Integer productType; // 1 = Electric energy, 2 = Natural Gas
    @Column(name ="contract_type")
    private Integer contractType; // 1 = Fix, 2 = E-go, 3 = Flex, 4 = Flux
    @Column(name ="commission_subcategory")
    private Integer commissionSubcategory; // 1 = B1, 2 = B2, 3 = B3, 4 = B4

    @Column(name ="start_delivery_date")
    private Date startDeliveryDate; // date when the service (energy or gas) starts

    @Column(name ="commission")
    private BigDecimal commission; // calculated commission

    @Column(name ="status")
    private Integer status; // 1 = Not Delivered to Admin, 2 = Delivered to Admin

    @Column(name ="agent_name")
    private String agentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id", nullable = false, insertable = false, updatable = false)
    private CountyEntity countyEntity;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public CountyEntity getCountyEntity() {
        return countyEntity;
    }

    public void setCountyEntity(CountyEntity countyEntity) {
        this.countyEntity = countyEntity;
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

    public BigInteger getAgentId() {
        return agentId;
    }

    public void setAgentId(BigInteger agentId) {
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

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
