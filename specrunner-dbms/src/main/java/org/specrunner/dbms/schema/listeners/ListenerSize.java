package org.specrunner.dbms.schema.listeners;

import org.specrunner.dbms.IPairListener;
import org.specrunner.dbms.Pair;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public class ListenerSize implements IPairListener<Table, Column> {

    @Override
    public String process(Pair<Table, Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case MAINTAIN:
            int old = pair.getOld().getSize();
            int current = pair.getCurrent().getSize();
            if (old != current) {
                sb.append("\t\t\tSIZE is " + old + " should be " + current + "\n");
            }
        default:
        }
        return sb.toString();

    }
}
