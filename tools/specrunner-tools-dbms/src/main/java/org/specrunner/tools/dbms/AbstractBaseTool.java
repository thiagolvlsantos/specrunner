/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.tools.dbms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.tools.dbms.core.PairingDefault;
import org.specrunner.tools.dbms.listeners.IColumnListener;
import org.specrunner.tools.dbms.listeners.ITableListener;

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

    protected String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date());
    }

    protected String process(String msg, ConnectionInfo old, ConnectionInfo current, ConfigurationFiles fileTableListeners, ConfigurationFiles fileColumnListeners) throws Exception {
        ConnectionDatabase pairOld = null;
        ConnectionDatabase pairCurrent = null;
        try {
            pairOld = newConnectionDatabase(old);
            Database databaseOld = pairOld.getDatabase();

            pairCurrent = newConnectionDatabase(current);
            Database databaseCurrent = pairCurrent.getDatabase();

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

    protected ConnectionDatabase newConnectionDatabase(ConnectionInfo ci) throws Exception {
        return new ConnectionDatabase(ci);
    }

    protected StringBuilder process(Database database1, Schema schema1, Database database2, Schema schema2, ConfigurationFiles fileTableListeners, ConfigurationFiles fileColumnListeners) {
        List<ITableListener> tableListeners = getTableListeners(fileTableListeners);
        List<IColumnListener> columnListeners = getColumnListeners(fileColumnListeners);

        Iterable<Pair<Table>> tables = new PairingDefault<Table>().pair(children(database1, schema1), children(database2, schema2), comparatorTable());
        Iterator<Pair<Table>> iterTables = tables.iterator();
        StringBuilder report = new StringBuilder();
        while (iterTables.hasNext()) {
            StringBuilder sbTable = new StringBuilder();
            boolean showTable = false;
            Pair<Table> table = iterTables.next();
            for (ITableListener lis : tableListeners) {
                IPart p = lis.process(table);
                if (p.hasData()) {
                    showTable = showTable || p.isMandatory();
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
                for (IColumnListener lis : columnListeners) {
                    IPart p = lis.process(column);
                    if (p.hasData()) {
                        showColumn = showColumn || p.isMandatory();
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

    protected List<ITableListener> getTableListeners(ConfigurationFiles fileTableListeners) {
        return load(ITableListener.class, fileTableListeners);
    }

    protected List<IColumnListener> getColumnListeners(ConfigurationFiles fileColumnListeners) {
        return load(IColumnListener.class, fileColumnListeners);
    }

    /**
     * Load object type from a file. Try first simple File reference, of not
     * exists, lookup in classpath.
     * 
     * @param type
     *            Class type of listed elements in file.
     * @param file
     *            File name to find.
     * @return A list of instances.
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> load(Class<T> type, ConfigurationFiles files) {
        List<T> result = new LinkedList<T>();
        for (String file : files) {
            InputStream in = null;
            BufferedReader br = null;
            InputStreamReader fr = null;
            try {
                File f = new File(file);
                if (!f.exists()) {
                    in = BaseComparator.class.getResourceAsStream(file);
                    if (in == null) {
                        throw new RuntimeException("Invalid configuration file (" + type + "): " + file);
                    }
                } else {
                    in = new FileInputStream(f);
                }
                fr = new InputStreamReader(in);
                br = new BufferedReader(fr);
                String input;
                while ((input = br.readLine()) != null) {
                    result.add((T) Class.forName(input.trim()).newInstance());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
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
