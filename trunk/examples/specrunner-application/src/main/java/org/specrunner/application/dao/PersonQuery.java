package org.specrunner.application.dao;

@SuppressWarnings("serial")
public class PersonQuery extends Query {

    private String firstName;
    private String lastName;
    private String[] types;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}