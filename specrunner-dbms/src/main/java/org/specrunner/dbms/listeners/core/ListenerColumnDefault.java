package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnDefault implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            sb.append("DEFAULT is " + pair.getCurrent().getDefaultValue());
            break;
        case MAINTAIN:
            Object old = pair.getOld().getDefaultValue();
            Object current = pair.getCurrent().getDefaultValue();
            if (!(old == null ? current == null : old.equals(current))) {
                sb.append("DEFAULT is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(true, sb.toString(), 3);
    }
}
