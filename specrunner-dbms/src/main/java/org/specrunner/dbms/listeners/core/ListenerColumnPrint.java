package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnPrint implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        sb.append(pair.getType() + " ");
        switch (pair.getType()) {
        case ADD:
            sb.append(pair.getCurrent().getFullName());
            break;
        case REMOVE:
            sb.append(pair.getOld().getFullName());
            break;
        case MAINTAIN:
            sb.append(pair.getOld().getFullName() + " <-> " + pair.getCurrent().getFullName());
            break;
        default:
        }
        return new PartDefault(sb.toString(), 2);
    }
}
