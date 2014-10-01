package org.specrunner.dbms;

public class Main {

    public static void main(String[] args) throws Exception {
        BaseComparator.main(new String[] { "PUBLIC.ORD",//
                "org.hsqldb.jdbcDriver",//
                "jdbc:hsqldb:hsql://localhost/sql",//
                "sa",//
                "",//
                "PUBLIC.DEP",//
                "org.hsqldb.jdbcDriver",//
                "jdbc:hsqldb:hsql://localhost/sql2",//
                "sa",//
                "" });
    }
}