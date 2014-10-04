package org.specrunner.dbms.core;

import org.specrunner.dbms.IPart;

public class PartDefault implements IPart {

    private boolean show;
    private String data;
    private int level;

    public PartDefault(boolean show, String data, int level) {
        this.show = show;
        this.data = data;
        this.level = level;
    }

    @Override
    public boolean show() {
        return show;
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