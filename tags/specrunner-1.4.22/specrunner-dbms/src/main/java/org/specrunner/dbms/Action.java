package org.specrunner.dbms;

public enum Action {

    ADD("+"), REMOVE("-"), MAINTAIN("=");

    private String code;

    private Action(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}