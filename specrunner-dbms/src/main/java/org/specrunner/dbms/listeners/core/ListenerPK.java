package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerPK implements IColumnListener {

    @Override
    public IPart process(String gap, Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case MAINTAIN:
            boolean old = pair.getOld().isPartOfPrimaryKey();
            boolean current = pair.getCurrent().isPartOfPrimaryKey();
            if (old != current) {
                sb.append(gap + "isPK is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(sb.toString());
    }
}
