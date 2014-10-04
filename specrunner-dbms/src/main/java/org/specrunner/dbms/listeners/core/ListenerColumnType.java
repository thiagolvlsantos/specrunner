package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnType implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            sb.append("TYPE is " + pair.getCurrent().getColumnDataType().getJavaSqlType().getJavaSqlTypeName());
            break;
        case MAINTAIN:
            String old = pair.getOld().getColumnDataType().getJavaSqlType().getJavaSqlTypeName();
            String current = pair.getCurrent().getColumnDataType().getJavaSqlType().getJavaSqlTypeName();
            if (!old.equalsIgnoreCase(current)) {
                sb.append("TYPE is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(true, sb.toString(), 3);
    }
}