package com.gcr.acm.customerservice.county;

import com.gcr.acm.jpaframework.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "county")
@Entity
public class CountyEntity extends EntityBase {

    @Column(name = "id")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name ="name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String description) {
        this.name = description;
    }

}
