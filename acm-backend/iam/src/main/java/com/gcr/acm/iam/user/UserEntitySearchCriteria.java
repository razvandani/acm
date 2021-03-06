package com.gcr.acm.iam.user;

import com.gcr.acm.jpaframework.EntitySearchCriteria;

import java.math.BigInteger;
import java.util.List;

/**
 * Entity search criteria for users.
 *
 * @author Razvan Dani
 */
public class UserEntitySearchCriteria extends EntitySearchCriteria {
    private String userName;
    private String email;
    private List<BigInteger> userIdList;
    private Boolean searchOnlyActive = true;
    private List<Integer> roleIdList;
    private String firstName;
    private String lastName;
    private List<Integer> statusIdList;
    private BigInteger partnerId;

    public Boolean getSearchOnlyActive() {
        return searchOnlyActive;
    }

    public void setSearchOnlyActive(Boolean searchOnlyActive) {
        this.searchOnlyActive = searchOnlyActive;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BigInteger> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<BigInteger> userIdList) {
        this.userIdList = userIdList;
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

    public List<Integer> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Integer> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public List<Integer> getStatusIdList() {
        return statusIdList;
    }

    public void setStatusIdList(List<Integer> statusIdList) {
        this.statusIdList = statusIdList;
    }

    public BigInteger getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(BigInteger partnerId) {
        this.partnerId = partnerId;
    }
}
