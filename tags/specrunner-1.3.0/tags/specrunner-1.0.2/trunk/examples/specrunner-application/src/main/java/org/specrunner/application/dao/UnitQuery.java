package org.specrunner.application.dao;

@SuppressWarnings("serial")
public class UnitQuery extends Query {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}