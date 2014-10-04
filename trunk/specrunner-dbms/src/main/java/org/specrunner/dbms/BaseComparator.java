package org.specrunner.dbms;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.core.PairingDefault;
import org.specrunner.dbms.listeners.IColumnListener;
import org.specrunner.dbms.listeners.ITableListener;
import org.specrunner.dbms.util.UtilIO;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

public class BaseComparator {

    public static void compare(ConnectionData old, ConnectionData current, String fileTableListeners, String fileColumnListeners) throws Exception {
        ConnectionDatabase pair1 = null;
        ConnectionDatabase pair2 = null;
        try {
            pair1 = ConnectionDatabase.newInstance(old);
            Database database1 = pair1.database;

            pair2 = ConnectionDatabase.newInstance(current);
            Database database2 = pair2.database;

            process(database1, database1.getSchema(old.getSchema()), database2, database2.getSchema(current.getSchema()), fileTableListeners, fileColumnListeners);
        } finally {
            pair1.finalize();
            pair2.finalize();
        }
    }

    private static void process(Database database1, Schema schema1, Database database2, Schema schema2, String fileTableListeners, String fileColumnListeners) {
        Iterable<Pair<Table>> tables = new PairingDefault<Table>().pair(children(database1, schema1), children(database2, schema2), comparatorTable());
        Iterator<Pair<Table>> iterTables = tables.iterator();
        StringBuilder report = new StringBuilder();
        while (iterTables.hasNext()) {
            Pair<Table> table = iterTables.next();
            StringBuilder sbTables = new StringBuilder();
            boolean showData = false;
            for (ITableListener lis : getTableListeners(fileTableListeners)) {
                IPart p = lis.process(table);
                if (p.hasData()) {
                    showData = showData || p.show();
                    sbTables.append(p.getData());
                }
            }
            Iterable<Pair<Column>> columns = new PairingDefault<Column>().pair(children(schema1, table.getOld()), children(schema2, table.getCurrent()), comparatorColumn());
            Iterator<Pair<Column>> iterColumns = columns.iterator();
            StringBuilder sbColumns = new StringBuilder();
            boolean showColumn = false;
            while (iterColumns.hasNext()) {
                Pair<Column> column = iterColumns.next();
                for (IColumnListener lis : getColumnListeners(fileColumnListeners)) {
                    IPart p = lis.process(column);
                    if (p.hasData()) {
                        showColumn = showColumn || p.show();
                        sbColumns.append(p.getData());
                    }
                }
            }
            report.append('\n');
            report.append(sbTables);
            report.append(sbColumns);
        }
        System.out.println(report);
    }

    private static List<ITableListener> getTableListeners(String file) {
        return UtilIO.load(ITableListener.class, file);
    }

    private static List<IColumnListener> getColumnListeners(String file) {
        return UtilIO.load(IColumnListener.class, file);
    }

    private static List<Table> children(Database database, Schema schema) {
        if (database == null || schema == null) {
            return new LinkedList<Table>();
        }
        List<Table> tables = new LinkedList<Table>(database.getTables(schema));
        Collections.sort(tables, comparatorTable());
        return tables;
    }

    private static Comparator<Table> comparatorTable() {
        return new Comparator<Table>() {
            @Override
            public int compare(Table o1, Table o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : (o2 == null ? -1 : o1.getName().compareToIgnoreCase(o2.getName()));
            }
        };
    }

    private static List<Column> children(Schema schema, Table table) {
        if (schema == null || table == null) {
            return new LinkedList<Column>();
        }
        List<Column> columns = table.getColumns();
        Collections.sort(columns, comparatorColumn());
        return columns;
    }

    private static Comparator<Column> comparatorColumn() {
        return new Comparator<Column>() {
            @Override
            public int compare(Column o1, Column o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : (o2 == null ? -1 : o1.getName().compareToIgnoreCase(o2.getName()));
            }
        };
    }
}