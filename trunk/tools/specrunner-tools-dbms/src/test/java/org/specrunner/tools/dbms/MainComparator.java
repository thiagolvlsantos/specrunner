package org.specrunner.tools.dbms;

import org.specrunner.tools.dbms.BaseComparator;
import org.specrunner.tools.dbms.ConfigurationFiles;
import org.specrunner.tools.dbms.ConnectionInfo;

public class MainComparator {

    public static void main(String[] args) throws Exception {
        ConnectionInfo old = new ConnectionInfo("PUBLIC.ORD", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/sql", "sa", "");
        ConnectionInfo current = new ConnectionInfo("PUBLIC.DEP", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/sql2", "sa", "");
        new BaseComparator().compare(old, current, new ConfigurationFiles("/sr_dbms_tables.properties"), new ConfigurationFiles("/sr_dbms_columns.properties"));
    }
}
