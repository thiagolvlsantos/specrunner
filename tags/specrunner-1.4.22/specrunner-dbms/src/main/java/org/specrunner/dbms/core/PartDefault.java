package org.specrunner.dbms.core;

import org.specrunner.dbms.IPart;

public class PartDefault implements IPart {

    private String data;
    private int level;

    public PartDefault(String data, int level) {
        this.data = data;
        this.level = level;
    }

    @Override
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }

    @Override
    public String getData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append('\t');
        }
        return sb + data + "\n";
    }

    @Override
    public int getLevel() {
        return level;
    }
}