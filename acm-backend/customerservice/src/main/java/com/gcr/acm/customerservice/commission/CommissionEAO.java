package com.gcr.acm.customerservice.commission;

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

		if (searchCriteria.getCommissionSubcategoryElectricCurrent() != null) {
			queryBuilder.addCondition(":commissionSubcategoryElectricCurrent BETWEEN ac.commissionSubcategoryStart AND ac.commissionSubcategoryEnd");
		}
	}

	public AgentCommissionEntity getAgentCommissionForNaturalGas(BigInteger agentId, Integer commissionType, Integer commissionSubcategory) {
		AgentCommissionEntitySearchCriteria agentCommissionEntitySearchCriteria = new AgentCommissionEntitySearchCriteria();
		agentCommissionEntitySearchCriteria.setAgentId(agentId);
		agentCommissionEntitySearchCriteria.setCommissionType(commissionType);
		agentCommissionEntitySearchCriteria.setCommissionSubcategory(commissionSubcategory);
		List<AgentCommissionEntity> agentCommissionEntityList = findAgentCommissions(agentCommissionEntitySearchCriteria);
		
		return agentCommissionEntityList.size() == 1 ? agentCommissionEntityList.get(0) : null;
	}

	public AgentCommissionEntity getAgentCommissionForElectricCurrent(
			BigInteger agentId, Integer commissionType, Integer commissionSubcategory) {
		AgentCommissionEntitySearchCriteria agentCommissionEntitySearchCriteria = new AgentCommissionEntitySearchCriteria();
		agentCommissionEntitySearchCriteria.setAgentId(agentId);
		agentCommissionEntitySearchCriteria.setCommissionType(commissionType);
		agentCommissionEntitySearchCriteria.setCommissionSubcategoryElectricCurrent(commissionSubcategory);

		List<AgentCommissionEntity> agentCommissionEntityList = findAgentCommissions(agentCommissionEntitySearchCriteria);

		return agentCommissionEntityList.size() == 1 ? agentCommissionEntityList.get(0) : null;
	}
}
