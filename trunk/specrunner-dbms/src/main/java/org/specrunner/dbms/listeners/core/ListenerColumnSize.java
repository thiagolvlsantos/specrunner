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
            sb.append("SIZE is " + pair.getCurrent().getSize());
            break;
        case MAINTAIN:
            int old = pair.getOld().getSize();
            int current = pair.getCurrent().getSize();
            if (old != current) {
                sb.append("SIZE is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(sb.toString(), 3);
    }
}
