package com.gcr.acm.customerservice.customer;

import com.gcr.acm.common.utils.Counter;
import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import com.gcr.acm.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Entity access object for customers.
 * 
 * @author Razvan Dani
 */
@Component
public class CustomerEAO extends EntityAccessObjectBase {

    /**
     * Saves the customer.
     *
     * @param customerEntity    The CustomerEntity
     * @return              The stored CustomerEntity
     */
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return storeEntity(customerEntity);
    }

    public CustomerEntity getCustomer(BigInteger id) {
        return getEntity(CustomerEntity.class, id);
    }
    
    /**
     * Finds the customers for the specified search criteria.
     *
     * @param searchCriteria    The CustomerEntitySearchCriteria
     * @return                  The List of CustomerEntity objects
     */
    public List<CustomerEntity> findCustomers(CustomerEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindCustomersBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindCustomersBuilder(CustomerEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("CustomerEntity", "c");

        populateQueryBuilderConditionsAndJoins(searchCriteria, queryBuilder);

        return queryBuilder;
    }

    private void populateQueryBuilderConditionsAndJoins(CustomerEntitySearchCriteria searchCriteria, JpaQueryBuilder queryBuilder) {
        queryBuilder.addInnerFetchJoin("c.countyEntity");

        if (searchCriteria.getStartDate() != null) {
            queryBuilder.addCondition("c.contractDate >= :startDate");
        }

        if (searchCriteria.getEndDate() != null) {
            queryBuilder.addCondition("c.contractDate <= :endDate");
        }

        if (searchCriteria.getDeliveryStartDate() != null) {
            queryBuilder.addCondition("c.startDeliveryDate >= :deliveryStartDate");
        }

        if (searchCriteria.getDeliveryEndDate() != null) {
            queryBuilder.addCondition("c.startDeliveryDate <= :deliveryEndDate");
        }

        if (searchCriteria.getProductType() != null) {
            queryBuilder.addCondition("c.productType = :productType");
        }

        if (searchCriteria.getFirstNameStartsWith() != null) {
            queryBuilder.addCondition("c.firstName LIKE :firstNameStartsWith");
        }

        if (searchCriteria.getLastNameStartsWith() != null) {
            queryBuilder.addCondition("c.lastName LIKE :lastNameStartsWith");
        }

        if (searchCriteria.getCountyId() != null) {
            queryBuilder.addCondition("c.countyId LIKE :countyId");
        }

        if (searchCriteria.getLocationStartsWith() != null) {
            queryBuilder.addCondition("c.location LIKE :locationStartsWith");
        }

        if (searchCriteria.getAgentId() != null) {
            queryBuilder.addCondition("c.agentId = :agentId");
        }

//        if (searchCriteria.getContractStartDate() != null && searchCriteria.getContractEndDate() != null) {
//            queryBuilder.addCondition("c.contractDate BETWEEN :contractStartDate AND :contractEndDate");
//        }

        if (searchCriteria.getStatus() != null) {
            queryBuilder.addCondition("c.status = :status");
        }

        queryBuilder.addOrderByStatement("c.contractDate, c.firstName, c.lastName");
    }

    public BigDecimal calculateCommission(CustomerEntitySearchCriteria customerEntitySearchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder(Counter.class);
        queryBuilder.addFromStatement("CustomerEntity c");
        queryBuilder.addSelectStatement("SUM(c.commission) AS count");
        populateQueryBuilderConditionsAndJoins(customerEntitySearchCriteria, queryBuilder);

        List<Counter> countersList = findObjects(queryBuilder, customerEntitySearchCriteria);
        return countersList.get(0).getCount();
    }
}
