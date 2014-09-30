package org.specrunner.dbms.schema.listeners;

import org.specrunner.dbms.IPairListener;
import org.specrunner.dbms.Pair;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class ListenerFK implements IPairListener<Table, Column> {

    @Override
    public String process(Pair<Table, Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case MAINTAIN:
            boolean old = pair.getOld().isPartOfForeignKey();
            boolean current = pair.getCurrent().isPartOfForeignKey();
            if (old != current) {
                sb.append("\t\t\tisFK is " + old + " should be " + current + "\n");
            }
        default:
        }
        return sb.toString();

    }
}
