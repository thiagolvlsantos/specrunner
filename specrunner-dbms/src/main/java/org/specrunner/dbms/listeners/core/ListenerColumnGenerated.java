package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnGenerated implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            sb.append("GENERATED is " + pair.getCurrent().isGenerated());
            break;
        case MAINTAIN:
            boolean old = pair.getOld().isGenerated();
            boolean current = pair.getCurrent().isGenerated();
            if (old != current) {
                sb.append("GENERATED is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(true, sb.toString(), 3);
    }
}
