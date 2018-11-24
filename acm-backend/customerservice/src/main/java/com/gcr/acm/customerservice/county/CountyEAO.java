package com.gcr.acm.customerservice.county;

import com.gcr.acm.common.utils.Utilities;
import com.gcr.acm.jpaframework.EntityAccessObjectBase;
import com.gcr.acm.jpaframework.JpaQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountyEAO extends EntityAccessObjectBase {


    public List<CountyEntity> findCounties(CountyEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = constructFindCountysBuilder(searchCriteria);

        return findEntities(queryBuilder, searchCriteria);
    }

    private JpaQueryBuilder constructFindCountysBuilder(CountyEntitySearchCriteria searchCriteria) {
        JpaQueryBuilder queryBuilder = new JpaQueryBuilder("CountyEntity", "c");

        populateCountyQueryBuilderConditions(searchCriteria, queryBuilder);

        return queryBuilder;
    }

    private void populateCountyQueryBuilderConditions(CountyEntitySearchCriteria searchCriteria, JpaQueryBuilder queryBuilder) {
        if (!Utilities.isEmptyOrNull(searchCriteria.getNameStartsWith())) {
            queryBuilder.addCondition("c.name LIKE :nameStartsWith");
        }

        queryBuilder.addOrderByStatement("c.name");
    }
    public CountyEntity getEmploymentType(Integer id) {
        return getEntity(CountyEntity.class, id);
    }

    public CountyEntity getCountyById(Integer countyId) {
        return getEntity(CountyEntity.class, countyId);
    }

}
