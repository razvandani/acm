package com.gcr.acm.customerservice.commission;

import com.gcr.acm.customerservice.customer.CustomerEntity;
import com.gcr.acm.customerservice.customer.CustomerEntitySearchCriteria;
import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import com.gcr.acm.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

/**
 * Entity access object for commissions.
 * 
 * @author Razvan Dani
 */
@Component
public class CommissionEAO extends EntityAccessObjectBase {

	/**
	 * Finds the agentCommissions for the specified search criteria.
	 *
	 * @param searchCriteria    The AgentCommissionEntitySearchCriteria
	 * @return                  The List of AgentCommissionEntity objects
	 */
	public List<AgentCommissionEntity> findAgentCommissions(AgentCommissionEntitySearchCriteria searchCriteria) {
		JpaQueryBuilder queryBuilder = constructFindAgentCommissionsBuilder(searchCriteria);

		return findEntities(queryBuilder, searchCriteria);
	}

	private JpaQueryBuilder constructFindAgentCommissionsBuilder(AgentCommissionEntitySearchCriteria searchCriteria) {
		JpaQueryBuilder queryBuilder = new JpaQueryBuilder("AgentCommissionEntity", "ac");

		populateQueryBuilderConditionsAndJoins(searchCriteria, queryBuilder);

		return queryBuilder;
	}

	private void populateQueryBuilderConditionsAndJoins(AgentCommissionEntitySearchCriteria searchCriteria, JpaQueryBuilder queryBuilder) {
		if (searchCriteria.getAgentId() != null) {
			queryBuilder.addCondition("ac.agentId = :agentId");
		}

		if (searchCriteria.getCommissionType() != null) {
			queryBuilder.addCondition("ac.commissionType = :commissionType");
		}

		if (searchCriteria.getCommissionSubcategory() != null) {
			queryBuilder.addCondition("ac.commissionSubcategory = :commissionSubcategory");
		}
	}

	public AgentCommissionEntity getAgentCommissionByPrimaryKey(BigInteger agentId, Integer commissionType, Integer commissionSubcategory) {
		AgentCommissionEntitySearchCriteria agentCommissionEntitySearchCriteria = new AgentCommissionEntitySearchCriteria();
		agentCommissionEntitySearchCriteria.setAgentId(agentId);
		agentCommissionEntitySearchCriteria.setCommissionType(commissionType);
		agentCommissionEntitySearchCriteria.setCommissionSubcategory(commissionSubcategory);
		List<AgentCommissionEntity> agentCommissionEntityList = findAgentCommissions(agentCommissionEntitySearchCriteria);
		
		return agentCommissionEntityList.size() == 1 ? agentCommissionEntityList.get(0) : null;
	}
	
	/**
	 * Finds the default commissions for the specified search criteria.
	 *
	 * @param searchCriteria    The DefaultCommissionEntitySearchCriteria
	 * @return                  The List of DefaultCommissionEntity objects
	 */
	public List<DefaultCommissionEntity> findDefaultCommissions(DefaultCommissionEntitySearchCriteria searchCriteria) {
		JpaQueryBuilder queryBuilder = constructFindDefaultCommissionsBuilder(searchCriteria);

		return findEntities(queryBuilder, searchCriteria);
	}

	private JpaQueryBuilder constructFindDefaultCommissionsBuilder(DefaultCommissionEntitySearchCriteria searchCriteria) {
		JpaQueryBuilder queryBuilder = new JpaQueryBuilder("DefaultCommissionEntity", "dc");

		populateQueryBuilderConditionsAndJoins(searchCriteria, queryBuilder);

		return queryBuilder;
	}

	private void populateQueryBuilderConditionsAndJoins(DefaultCommissionEntitySearchCriteria searchCriteria, JpaQueryBuilder queryBuilder) {
		if (searchCriteria.getCommissionType() != null) {
			queryBuilder.addCondition("dc.commissionType = :commissionType");
		}

		if (searchCriteria.getCommissionSubcategory() != null) {
			queryBuilder.addCondition("dc.commissionSubcategory = :commissionSubcategory");
		}
	}

	public DefaultCommissionEntity getDefaultCommissionByPrimaryKey(Integer commissionType, Integer commissionSubcategory) {
		DefaultCommissionEntitySearchCriteria defaultCommissionEntitySearchCriteria = new DefaultCommissionEntitySearchCriteria();
		defaultCommissionEntitySearchCriteria.setCommissionType(commissionType);
		defaultCommissionEntitySearchCriteria.setCommissionSubcategory(commissionSubcategory);
		List<DefaultCommissionEntity> defaultCommissionEntityList = findDefaultCommissions(defaultCommissionEntitySearchCriteria);

		return defaultCommissionEntityList.size() == 1 ? defaultCommissionEntityList.get(0) : null;
	}
}
