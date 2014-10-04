package org.specrunner.dbms;

public class Main {

    public static void main(String[] args) throws Exception {
        BaseComparator.compare(new ConnectionData("PUBLIC.ORD",//
                "org.hsqldb.jdbcDriver",//
                "jdbc:hsqldb:hsql://localhost/sql",//
                "sa",//
                ""),//
                new ConnectionData("PUBLIC.DEP",//
                        "org.hsqldb.jdbcDriver",//
                        "jdbc:hsqldb:hsql://localhost/sql2",//
                        "sa",//
                        ""),//
                "/sr_dbms_tables.properties", "/sr_dbms_columns.properties");
    }
}