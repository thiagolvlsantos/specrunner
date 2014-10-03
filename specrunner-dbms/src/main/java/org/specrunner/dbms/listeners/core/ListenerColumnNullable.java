package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnNullable implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            sb.append("NULLABLE is " + pair.getCurrent().isNullable());
            break;
        case MAINTAIN:
            boolean old = pair.getOld().isNullable();
            boolean current = pair.getCurrent().isNullable();
            if (old != current) {
                sb.append("NULLABLE is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(sb.toString(), 3);
    }
}
