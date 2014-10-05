package org.specrunner.dbms;

public class Main {

    public static void main(String[] args) throws Exception {
        ConnectionInfo old = new ConnectionInfo("PUBLIC.ORD", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/sql", "sa", "");
        ConnectionInfo current = new ConnectionInfo("PUBLIC.DEP", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/sql2", "sa", "");
        new BaseComparator().compare(old, current, "/sr_dbms_tables.properties", "/sr_dbms_columns.properties");
    }
}