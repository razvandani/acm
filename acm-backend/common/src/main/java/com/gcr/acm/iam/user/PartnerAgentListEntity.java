package com.gcr.acm.iam.user;

import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * JPA entity for partner_agent_list table.
 *
 * @author Razvan Dani
 */
@Table(name = "partner_agent_list")
@Entity
public class PartnerAgentListEntity extends EntityBase {
    @Column(name = "id")
    @Id
    private BigInteger id;

    @Column(name ="partner_id")
    private String partnerId;

    @Column(name ="agent_id")
    private String agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parner_id", nullable = false)
    private UserEntity partnerEntity;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public UserEntity getPartnerEntity() {
        return partnerEntity;
    }

    public void setPartnerEntity(UserEntity partnerEntity) {
        this.partnerEntity = partnerEntity;
    }
}
