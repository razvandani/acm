package com.gcr.acm.iam.user;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Encapsulates user information.
 *
 * @author Razvan Dani
 */
public class UserInfo {
    public static final Integer ROLE_ID_SUPER_USER = 1;
    public static final Integer ROLE_ID_AGENT = 2;

    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String encryptedPassword;
    private Date createdDttm;
    private Boolean isActive;
    private Boolean isLocked;
    private Integer roleId;
    private String phoneNumber;

    private String errorMessage; // readonly
    private String loginToken; // readonly
    private String newPassword; // write only
    // todo list of agent commissions
    private List<AgentCommissionInfo> agentCommissionInfoList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedDttm() {
        return createdDttm;
    }

    public void setCreatedDttm(Date createdDttm) {
        this.createdDttm = createdDttm;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isSuperUser() {
        return ROLE_ID_SUPER_USER.equals(roleId);
    }

    public boolean isAgent() {
        return ROLE_ID_AGENT.equals(roleId);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public List<AgentCommissionInfo> getAgentCommissionInfoList() {
        return agentCommissionInfoList;
    }

    public void setAgentCommissionInfoList(List<AgentCommissionInfo> agentCommissionInfoList) {
        this.agentCommissionInfoList = agentCommissionInfoList;
    }

    public static class AgentCommissionInfo {
        private Integer id;
        private Integer commissionType;
        private Integer commissionSubcategory;
        private Integer commissionSubcategoryStart;
        private Integer commissionSubcategoryEnd;
        private BigDecimal commissionValue;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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

        public Integer getCommissionSubcategoryStart() {
            return commissionSubcategoryStart;
        }

        public void setCommissionSubcategoryStart(Integer commissionSubcategoryStart) {
            this.commissionSubcategoryStart = commissionSubcategoryStart;
        }

        public Integer getCommissionSubcategoryEnd() {
            return commissionSubcategoryEnd;
        }

        public void setCommissionSubcategoryEnd(Integer commissionSubcategoryEnd) {
            this.commissionSubcategoryEnd = commissionSubcategoryEnd;
        }

        public BigDecimal getCommissionValue() {
            return commissionValue;
        }

        public void setCommissionValue(BigDecimal commissionValue) {
            this.commissionValue = commissionValue;
        }


    }
}
