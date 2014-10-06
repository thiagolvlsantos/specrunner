package org.specrunner.dbms;

public class MainAnalyser {

    public static void main(String[] args) throws Exception {
        ConnectionInfo current = new ConnectionInfo("PUBLIC.DEP", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/sql2", "sa", "");
        new BaseAnalyser().analyse(current, "/sr_dbms_tables.properties", "/sr_dbms_columns.properties");
    }
}