package org.specrunner.dbms;

public class Main {

    public static void main(String[] args) throws Exception {
        new BaseComparator().compare(new ConnectionInfo("PUBLIC.ORD",//
                "org.hsqldb.jdbcDriver",//
                "jdbc:hsqldb:hsql://localhost/sql",//
                "sa",//
                ""),//
                new ConnectionInfo("PUBLIC.DEP",//
                        "org.hsqldb.jdbcDriver",//
                        "jdbc:hsqldb:hsql://localhost/sql2",//
                        "sa",//
                        ""),//
                "/sr_dbms_tables.properties", "/sr_dbms_columns.properties");
    }
}