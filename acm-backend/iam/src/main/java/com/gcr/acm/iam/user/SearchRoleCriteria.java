package com.gcr.acm.iam.user;

/**
 * Criteria class for searching roles.
 *
 * @author Razvan Dani
 */
public class SearchRoleCriteria {
    private boolean searchOnlyNotDeleted;
    private Integer startIndex;
    private Integer pageSize;

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isSearchOnlyNotDeleted() {
        return searchOnlyNotDeleted;
    }

    public void setSearchOnlyNotDeleted(boolean searchOnlyNotDeleted) {
        this.searchOnlyNotDeleted = searchOnlyNotDeleted;
    }
}
