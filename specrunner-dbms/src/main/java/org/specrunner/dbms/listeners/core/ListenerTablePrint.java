package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.ITableListener;

import schemacrawler.schema.Table;

public class ListenerTablePrint implements ITableListener {

    @Override
    public IPart process(Pair<Table> pair) {
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
        return new PartDefault(false, sb.toString(), 1);
    }
}
