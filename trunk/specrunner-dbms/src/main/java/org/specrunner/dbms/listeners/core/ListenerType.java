package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerType implements IColumnListener {

    @Override
    public IPart process(String gap, Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case MAINTAIN:
            String old = pair.getOld().getColumnDataType().getName();
            String current = pair.getCurrent().getColumnDataType().getName();
            if (!old.equalsIgnoreCase(current)) {
                sb.append(gap + "TYPE is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(sb.toString());
    }
}