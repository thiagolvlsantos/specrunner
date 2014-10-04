package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnSize implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            int add = pair.getCurrent().getColumnDataType().getJavaSqlType().getJavaSqlType();
            if (add == java.sql.Types.CHAR || add == java.sql.Types.VARCHAR) {
                sb.append("SIZE is " + pair.getCurrent().getSize());
            }
            break;
        case MAINTAIN:
            int type = pair.getOld().getColumnDataType().getJavaSqlType().getJavaSqlType();
            if (type == java.sql.Types.CHAR || type == java.sql.Types.VARCHAR) {
                int old = pair.getOld().getSize();
                int current = pair.getCurrent().getSize();
                if (old != current) {
                    sb.append("SIZE is " + old + " should be " + current);
                }
            }
        default:
        }
        return new PartDefault(true, sb.toString(), 3);
    }
}
