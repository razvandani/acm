package com.gcr.acm.methodcache;

public class ResultObjectContainer {
    private String resultObjectAsString;
    private boolean isCollection;
    private String collectionClassName;
    private String resultObjectClassName;
    private Object resultObject;

    public ResultObjectContainer() {
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

    public String getResultObjectAsString() {
        return resultObjectAsString;
    }

    public void setResultObjectAsString(String resultObjectAsString) {
        this.resultObjectAsString = resultObjectAsString;
    }

    public boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(boolean collection) {
        isCollection = collection;
    }

    public String getCollectionClassName() {
        return collectionClassName;
    }

    public void setCollectionClassName(String collectionClassName) {
        this.collectionClassName = collectionClassName;
    }

    public String getResultObjectClassName() {
        return resultObjectClassName;
    }

    public void setResultObjectClassName(String resultObjectClassName) {
        this.resultObjectClassName = resultObjectClassName;
    }
}
