package com.gcr.acm.iam.user;

import java.util.List;

/**
 * Search criteria class for users.
 *
 * @author Razvan Dani
 */
public class SearchUserCriteria {
    private Boolean searchOnlyActive = true;
    private List<String> userIdList;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Integer roleId;
    private Integer startIndex;
    private Integer pageSize;

    public Boolean getSearchOnlyActive() {
        return searchOnlyActive;
    }

    public void setSearchOnlyActive(Boolean searchOnlyActive) {
        this.searchOnlyActive = searchOnlyActive;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
