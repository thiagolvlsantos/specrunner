package org.specrunner.dbms.schema.listeners;

import org.specrunner.dbms.IPairListener;
import org.specrunner.dbms.Pair;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class ListenerNullable implements IPairListener<Table, Column> {

    @Override
    public String process(Pair<Table, Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case MAINTAIN:
            boolean old = pair.getOld().isNullable();
            boolean current = pair.getCurrent().isNullable();
            if (old != current) {
                sb.append("\t\t\tNULLABLE is " + old + " should be " + current + "\n");
            }
        default:
        }
        return sb.toString();

    }
}
