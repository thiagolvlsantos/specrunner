/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
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

/**
 * Perform base operations.
 * 
 * @author Thiago Santos
 */
public abstract class AbstractBaseTool {

    protected String process(String msg, ConnectionInfo old, ConnectionInfo current, String fileTableListeners, String fileColumnListeners) throws Exception {
        ConnectionDatabase pairOld = null;
        ConnectionDatabase pairCurrent = null;
        try {
            pairOld = new ConnectionDatabase(old);
            Database databaseOld = pairOld.database;

            pairCurrent = new ConnectionDatabase(current);
            Database databaseCurrent = pairCurrent.database;

            StringBuilder sb = process(databaseOld, databaseOld.getSchema(old.getSchema()), databaseCurrent, databaseCurrent.getSchema(current.getSchema()), fileTableListeners, fileColumnListeners);
            if (sb.length() > 0) {
                return sb.toString();
            } else {
                return msg;
            }
        } finally {
            if (pairOld != null) {
                pairOld.finalize();
            }
            if (pairCurrent != null) {
                pairCurrent.finalize();
            }
        }
    }

    protected StringBuilder process(Database database1, Schema schema1, Database database2, Schema schema2, String fileTableListeners, String fileColumnListeners) {
        Iterable<Pair<Table>> tables = new PairingDefault<Table>().pair(children(database1, schema1), children(database2, schema2), comparatorTable());
        Iterator<Pair<Table>> iterTables = tables.iterator();
        StringBuilder report = new StringBuilder();
        while (iterTables.hasNext()) {
            StringBuilder sbTable = new StringBuilder();
            boolean showTable = false;
            Pair<Table> table = iterTables.next();
            for (ITableListener lis : getTableListeners(fileTableListeners)) {
                IPart p = lis.process(table);
                if (p.hasData()) {
                    showTable = showTable || p.isOptional();
                    sbTable.append(p.getData());
                }
            }
            Iterable<Pair<Column>> columns = new PairingDefault<Column>().pair(children(schema1, table.getOld()), children(schema2, table.getCurrent()), comparatorColumn());
            Iterator<Pair<Column>> iterColumns = columns.iterator();
            StringBuilder sbColumns = new StringBuilder();
            boolean showColumns = false;
            while (iterColumns.hasNext()) {
                StringBuilder sbColumn = new StringBuilder();
                boolean showColumn = false;
                Pair<Column> column = iterColumns.next();
                for (IColumnListener lis : getColumnListeners(fileColumnListeners)) {
                    IPart p = lis.process(column);
                    if (p.hasData()) {
                        showColumn = showColumn || p.isOptional();
                        sbColumn.append(p.getData());
                    }
                }
                if (showColumn) {
                    sbColumns.append(sbColumn);
                    showColumns = true;
                }
            }
            if (showTable || showColumns) {
                report.append('\n');
                report.append(sbTable);
            }
            if (showColumns) {
                report.append(sbColumns);
            }
        }
        return report;
    }

    protected List<ITableListener> getTableListeners(String file) {
        return UtilIO.load(ITableListener.class, file);
    }

    protected List<IColumnListener> getColumnListeners(String file) {
        return UtilIO.load(IColumnListener.class, file);
    }

    protected List<Table> children(Database database, Schema schema) {
        if (database == null || schema == null) {
            return new LinkedList<Table>();
        }
        List<Table> tables = new LinkedList<Table>(database.getTables(schema));
        Collections.sort(tables, comparatorTable());
        return tables;
    }

    protected Comparator<Table> comparatorTable() {
        return new Comparator<Table>() {
            @Override
            public int compare(Table o1, Table o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : (o2 == null ? -1 : o1.getName().compareToIgnoreCase(o2.getName()));
            }
        };
    }

    protected List<Column> children(Schema schema, Table table) {
        if (schema == null || table == null) {
            return new LinkedList<Column>();
        }
        List<Column> columns = table.getColumns();
        Collections.sort(columns, comparatorColumn());
        return columns;
    }

    protected Comparator<Column> comparatorColumn() {
        return new Comparator<Column>() {
            @Override
            public int compare(Column o1, Column o2) {
                return o1 == null ? (o2 == null ? 0 : 1) : (o2 == null ? -1 : o1.getName().compareToIgnoreCase(o2.getName()));
            }
        };
    }
}