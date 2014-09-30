package org.specrunner.dbms.schema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.Action;
import org.specrunner.dbms.IAnalyser;
import org.specrunner.dbms.IAnalyserManager;
import org.specrunner.dbms.IPairListener;
import org.specrunner.dbms.IPairListenerManager;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.schema.listeners.ListenerFK;
import org.specrunner.dbms.schema.listeners.ListenerNullable;
import org.specrunner.dbms.schema.listeners.ListenerPK;
import org.specrunner.dbms.schema.listeners.ListenerSize;
import org.specrunner.dbms.schema.listeners.ListenerType;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

public class AnalyserDatabase {

    @SuppressWarnings({ "unchecked" })
    public static void main(String[] args) throws Exception {
        final String sch = args[0];
        final String drv = args[1];
        final String url = args[2];
        final String usr = args[3];
        final String pwd = args[4];
        Pair<Connection, Database> pair = getDatabase(sch, drv, url, usr, pwd);
        Connection connection = pair.getParentCurrent();
        Database database = pair.getCurrent();

        final String sch2 = args[5];
        final String drv2 = args[6];
        final String url2 = args[7];
        final String usr2 = args[8];
        final String pwd2 = args[9];
        Pair<Connection, Database> pair2 = getDatabase(sch2, drv2, url2, usr2, pwd2);
        Connection connection2 = pair2.getParentCurrent();
        Database database2 = pair2.getCurrent();

        IAnalyser<Database, Schema> an = new AnalyserSchema();
        IPairListenerManager manager = new IPairListenerManager() {
            @Override
            public <S, T> List<IPairListener<S, T>> get(Class<S> s, Class<T> t) {
                List<IPairListener<S, T>> result = new LinkedList<IPairListener<S, T>>();
                if (s == Schema.class && t == Table.class) {
                    result.add((IPairListener<S, T>) new IPairListener<Schema, Table>() {
                        @Override
                        public String process(Pair<Schema, Table> pair) {
                            return ("\t" + pair + '\n');
                        }
                    });
                }
                if (s == Table.class && t == Column.class) {
                    result.add((IPairListener<S, T>) new IPairListener<Table, Column>() {

                        @Override
                        public String process(Pair<Table, Column> pair) {
                            return ("\t\t" + pair + '\n');
                        }
                    });
                    result.add((IPairListener<S, T>) new ListenerPK());
                    result.add((IPairListener<S, T>) new ListenerFK());
                    result.add((IPairListener<S, T>) new ListenerNullable());
                    result.add((IPairListener<S, T>) new ListenerType());
                    result.add((IPairListener<S, T>) new ListenerSize());
                }
                return result;
            }
        };
        IAnalyserManager analysers = new IAnalyserManager() {

            @Override
            public <S, T> List<IAnalyser<S, T>> get(Class<S> s, Class<T> t) {
                List<IAnalyser<S, T>> result = new LinkedList<IAnalyser<S, T>>();
                if (s == Schema.class && t == Table.class) {
                    result.add((IAnalyser<S, T>) new AnalyserTable());
                }
                if (s == Table.class && t == Column.class) {
                    result.add((IAnalyser<S, T>) new AnalyserColumn());
                }
                return result;
            }
        };
        String report = an.analyse(Arrays.asList(new Pair<Database, Schema>(Action.MAINTAIN, database, database.getSchema(sch), database2, database2.getSchema(sch2))), analysers, manager);
        System.out.println("REPORT:\n" + report);

        connection.close();
        connection2.close();
    }

    @SuppressWarnings("serial")
    private static Pair<Connection, Database> getDatabase(final String sch, final String drv, final String url, final String usr, final String pwd) throws ClassNotFoundException, SQLException, SchemaCrawlerException {
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
        return new Pair<Connection, Database>(Action.MAINTAIN, null, null, connection, SchemaCrawlerUtility.getDatabase(connection, options));
    }
}
