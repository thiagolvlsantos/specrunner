package org.specrunner.dbms.schema;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.IAnalyser;
import org.specrunner.dbms.IAnalyserManager;
import org.specrunner.dbms.IPairListenerManager;
import org.specrunner.dbms.IPairing;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.AbstractAnalyser;
import org.specrunner.dbms.core.PairingDefault;

import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

public class AnalyserTable extends AbstractAnalyser<Schema, Table> {

    @Override
    public String analyse(Iterable<Pair<Schema, Table>> data, IAnalyserManager analysers, IPairListenerManager manager) {
        StringBuilder sb = new StringBuilder();
        Iterator<Pair<Schema, Table>> iter = data.iterator();
        while (iter.hasNext()) {
            Pair<Schema, Table> next = iter.next();
            Schema parentOld = next.getParentOld();
            Table old = next.getOld();
            Schema parentCur = next.getParentCurrent();
            Table cur = next.getCurrent();
            IPairing<Table, Column> pairing = new PairingDefault<Table, Column>();
            Iterable<Pair<Table, Column>> tables = pairing.pair(old, columns(parentOld, old), cur, columns(parentCur, cur), comparator());
            List<IAnalyser<Table, Column>> analyseres = analysers.get(Table.class, Column.class);
            StringBuilder child = new StringBuilder();
            for (IAnalyser<Table, Column> an : analyseres) {
                an.add(manager.get(Table.class, Column.class));
                child.append(an.analyse(tables, analysers, manager));
            }
            if (child.length() > 0) {
                sb.append(fireProcess(next));
                sb.append(child);
            }
        }
        return sb.toString();
    }

    protected List<Column> columns(Schema schema, Table table) {
        if (schema == null || table == null) {
            return new LinkedList<Column>();
        }
        List<Column> columns = table.getColumns();
        Collections.sort(columns, comparator());
        return columns;
    }

    protected Comparator<Column> comparator() {
        return new Comparator<Column>() {
            @Override
            public int compare(Column o1, Column o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : (o2 == null ? -1 : o1.getName().compareToIgnoreCase(o2.getName()));
            }
        };
    }
}