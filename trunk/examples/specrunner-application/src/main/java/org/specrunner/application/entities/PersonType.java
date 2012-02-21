package org.specrunner.application.entities;

public enum PersonType {

    PERSON(0, "Person"), EMPLOYEE(1, "Employee");

    private int code;
    private String name;

    private PersonType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}