package org.specrunner.dbms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.core.PairingDefault;
import org.specrunner.dbms.listeners.IColumnListener;
import org.specrunner.dbms.listeners.ITableListener;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

public class BaseComparator {

    public static void main(String[] args) throws Exception {
        String sch1 = args[0];
        String drv1 = args[1];
        String url1 = args[2];
        String usr1 = args[3];
        String pwd1 = args[4];
        Config pair1 = getDatabase(sch1, drv1, url1, usr1, pwd1);
        Connection connection1 = pair1.connnection;
        Database database1 = pair1.database;

        String sch2 = args[5];
        String drv2 = args[6];
        String url2 = args[7];
        String usr2 = args[8];
        String pwd2 = args[9];
        Config pair2 = getDatabase(sch2, drv2, url2, usr2, pwd2);
        Connection connection2 = pair2.connnection;
        Database database2 = pair2.database;

        process(database1, database1.getSchema(sch1), database2, database2.getSchema(sch2));

        connection1.close();
        connection2.close();
    }

    private static class Config {
        public Connection connnection;
        public Database database;

        public Config(Connection connnection, Database database) {
            this.connnection = connnection;
            this.database = database;
        }
    }

    @SuppressWarnings("serial")
    private static Config getDatabase(final String sch, final String drv, final String url, final String usr, final String pwd) throws ClassNotFoundException, SQLException, SchemaCrawlerException {
        final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
        options.setSchemaInclusionRule(new InclusionRule() {
            @Override
            public boolean include(String text) {
                return text.equalsIgnoreCase(sch);
            }
        });
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
        Class.forName(drv);
        Connection connection = DriverManager.getConnection(url, usr, pwd);
        return new Config(connection, SchemaCrawlerUtility.getDatabase(connection, options));
    }

    private static void process(Database database1, Schema schema1, Database database2, Schema schema2) {
        Iterable<Pair<Table>> tables = new PairingDefault<Table>().pair(children(database1, schema1), children(database2, schema2), comparatorTable());
        Iterator<Pair<Table>> iterTables = tables.iterator();
        StringBuilder report = new StringBuilder();
        while (iterTables.hasNext()) {
            Pair<Table> table = iterTables.next();
            StringBuilder sbTables = new StringBuilder();
            for (ITableListener lis : getTableListeners()) {
                IPart p = lis.process(table);
                if (p.hasData()) {
                    sbTables.append(p.getData());
                }
            }
            Iterable<Pair<Column>> columns = new PairingDefault<Column>().pair(children(schema1, table.getOld()), children(schema2, table.getCurrent()), comparatorColumn());
            Iterator<Pair<Column>> iterColumns = columns.iterator();
            StringBuilder sbColumns = new StringBuilder();
            while (iterColumns.hasNext()) {
                Pair<Column> column = iterColumns.next();
                for (IColumnListener lis : getColumnListeners()) {
                    IPart p = lis.process(column);
                    if (p.hasData()) {
                        sbColumns.append(p.getData());
                    }
                }
            }
            if (sbColumns.length() > 0) {
                report.append(sbTables);
                report.append(sbColumns);
            }
        }
        System.out.println(report);
    }

    private static List<ITableListener> getTableListeners() {
        return load(ITableListener.class, "/sr_tables.properties");
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> load(Class<T> type, String file) {
        List<T> result = new LinkedList<T>();
        InputStream in = null;
        BufferedReader br = null;
        InputStreamReader fr = null;
        try {
            in = BaseComparator.class.getResourceAsStream(file);
            if (in == null) {
                throw new RuntimeException("Invalid configuration file (" + type + "): " + file);
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
        return result;
    }

    private static List<IColumnListener> getColumnListeners() {
        return load(IColumnListener.class, "/sr_columns.properties");
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