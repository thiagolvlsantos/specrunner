package org.specrunner.dbms.core;

import org.specrunner.dbms.IPart;

public class PartDefault implements IPart {

    private String data;

    public PartDefault(String data) {
        this.data = data;
    }

    @Override
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }

    @Override
    public String getData() {
        return data;
    }
}