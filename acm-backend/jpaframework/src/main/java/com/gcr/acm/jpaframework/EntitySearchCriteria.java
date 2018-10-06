package com.gcr.acm.jpaframework;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * Base class for all entity search criteria objects. It providers methods for setting and accessing the maximum
 * results returned by a query and the start index of the results.
 *
 * @author Razvan Dani
 */
public abstract class EntitySearchCriteria {
    private Integer maxResults; // the max number of results to be returned by a query, null means no maximum
    private Integer startResultIndex; // the start index of the results returned by the query, null or 0 means start from the beginning
    private List<OrderByInfo> orderByInfoList; //the order by list, the search result will be ordered based on the OrderByInfos from this list
    private LockModeType lockModeType = LockModeType.PESSIMISTIC_READ; // the LockModeType

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Integer getStartResultIndex() {
        return startResultIndex;
    }

    public void setStartResultIndex(Integer startResultIndex) {
        this.startResultIndex = startResultIndex;
    }

    public List<OrderByInfo> getOrderByInfoList() {
        return orderByInfoList;
    }

    public void setOrderByInfoList(List<OrderByInfo> orderByInfoList) {
        this.orderByInfoList = orderByInfoList;
    }

    public LockModeType getLockModeType() {
        return lockModeType;
    }

    public void setLockModeType(LockModeType lockModeType) {
        this.lockModeType = lockModeType;
    }
}
