package org.specrunner.dbms.schema.listeners;

import org.specrunner.dbms.IPairListener;
import org.specrunner.dbms.Pair;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class ListenerType implements IPairListener<Table, Column> {

    @Override
    public String process(Pair<Table, Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case MAINTAIN:
            String old = pair.getOld().getColumnDataType().getName();
            String current = pair.getCurrent().getColumnDataType().getName();
            if (!old.equalsIgnoreCase(current)) {
                sb.append("\t\t\tTYPE is " + old + " should be " + current + "\n");
            }
        default:
        }
        return sb.toString();

    }
}
