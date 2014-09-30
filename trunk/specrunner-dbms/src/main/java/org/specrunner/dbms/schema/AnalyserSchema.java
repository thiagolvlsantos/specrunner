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

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

public class AnalyserSchema extends AbstractAnalyser<Database, Schema> {

    @Override
    public String analyse(Iterable<Pair<Database, Schema>> data, IAnalyserManager analysers, IPairListenerManager manager) {
        StringBuilder sb = new StringBuilder();
        Iterator<Pair<Database, Schema>> iter = data.iterator();
        while (iter.hasNext()) {
            Pair<Database, Schema> next = iter.next();
            Database parentOld = next.getParentOld();
            Schema old = next.getOld();
            Database parentCur = next.getParentCurrent();
            Schema cur = next.getCurrent();
            IPairing<Schema, Table> pairing = new PairingDefault<Schema, Table>();
            Iterable<Pair<Schema, Table>> tables = pairing.pair(old, children(parentOld, old), cur, children(parentCur, cur), comparator());
            List<IAnalyser<Schema, Table>> analyseres = analysers.get(Schema.class, Table.class);
            StringBuilder child = new StringBuilder();
            for (IAnalyser<Schema, Table> an : analyseres) {
                an.add(manager.get(Schema.class, Table.class));
                child.append(an.analyse(tables, analysers, manager));
            }
            if (child.length() > 0) {
                sb.append(fireProcess(next));
                sb.append(child);
            }
        }
        return sb.toString();
    }

    protected List<Table> children(Database database, Schema schema) {
        if (database == null || schema == null) {
            return new LinkedList<Table>();
        }
        List<Table> tables = new LinkedList<Table>(database.getTables(schema));
        Collections.sort(tables, comparator());
        return tables;
    }

    protected Comparator<Table> comparator() {
        return new Comparator<Table>() {
            @Override
            public int compare(Table o1, Table o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : (o2 == null ? -1 : o1.getName().compareToIgnoreCase(o2.getName()));
            }
        };
    }
}