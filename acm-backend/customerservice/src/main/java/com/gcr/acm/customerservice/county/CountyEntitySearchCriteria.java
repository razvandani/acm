package com.gcr.acm.customerservice.county;

import com.gcr.acm.jpaframework.EntitySearchCriteria;

import java.util.List;

public class CountyEntitySearchCriteria extends EntitySearchCriteria {

    private String nameStartsWith;

    public String getNameStartsWith() {
        return nameStartsWith;
    }

    public void setNameStartsWith(String nameStartsWith) {
        this.nameStartsWith = nameStartsWith;
    }
}
