package com.gcr.acm.iam.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gcr.acm.customerservice.commission.AgentCommissionEntity;
import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

/**
 * JPA entity for user table.
 *
 * @author Razvan Dani
 */
@Table(name = "user")
@Entity
public class UserEntity extends EntityBase {
    @Column(name = "user_id")
    @Id
    private BigInteger userId;

    @Column(name ="user_name")
    private String username;

    @Column(name ="email")
    private String email;

    @Column(name ="first_name")
    private String firstName;

    @Column(name ="last_name")
    private String lastName;

    @Column(name ="password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name ="created_dttm")
    private Date createdDttm;

    // will be set to is_active by default
    @Column(name = "is_active")
    private Boolean isActive;

    // behind the scenes, in case of multiple invalid password attempts, this is set to true. afterwards,
    // logins are not possible until super user manually unlocks
    @Column(name = "is_locked")
    private Boolean isLocked;

    // set behind the scenes to role id 2, when an agent is created by super user. Otherwise, the only the other role id is 1,
    // which means super user. Only super users can create other super users, but this is not part of the intial version when it comes to UI,
    // even though in the backend this feature is already supported.
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name ="phone_number")
    private String phoneNumber;

    @Column(name ="reset_password_token")
    private String resetPasswordToken;

    @Column(name ="agent_abbreviation")
    private String agentAbbreviation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, insertable = false, updatable = false)
    private RoleEntity roleEntity;

    @OneToMany(mappedBy = "agentEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AgentCommissionEntity> agentCommissionEntitySet;

    @OneToMany(mappedBy = "partnerEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartnerAgentListEntity> partnerAgentListEntitySet;

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
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

    public RoleEntity getRoleEntity() {
        return roleEntity;
    }

    public void setRoleEntity(RoleEntity roleEntity) {
        this.roleEntity = roleEntity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<AgentCommissionEntity> getAgentCommissionEntitySet() {
        return agentCommissionEntitySet;
    }

    public void setAgentCommissionEntitySet(Set<AgentCommissionEntity> agentCommissionEntitySet) {
        this.agentCommissionEntitySet = agentCommissionEntitySet;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public String getAgentAbbreviation() {
        return agentAbbreviation;
    }

    public void setAgentAbbreviation(String agentAbbreviation) {
        this.agentAbbreviation = agentAbbreviation;
    }

    public Set<PartnerAgentListEntity> getPartnerAgentListEntitySet() {
        return partnerAgentListEntitySet;
    }

    public void setPartnerAgentListEntitySet(Set<PartnerAgentListEntity> partnerAgentListEntitySet) {
        this.partnerAgentListEntitySet = partnerAgentListEntitySet;
    }
}
