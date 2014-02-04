package org.specrunner.application.dao;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Query implements Serializable {

    private String property;
    private Boolean ascending;
    private Integer first;
    private Integer count;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Boolean getAscending() {
        return ascending;
    }

    public void setAscending(Boolean ascending) {
        this.ascending = ascending;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
