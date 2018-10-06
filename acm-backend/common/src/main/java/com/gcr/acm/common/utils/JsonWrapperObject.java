package com.gcr.acm.common.utils;

/**
 * @author Razvan Dani.
 */
public class JsonWrapperObject {

    private String value;

    public JsonWrapperObject() {
    }

    public JsonWrapperObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
