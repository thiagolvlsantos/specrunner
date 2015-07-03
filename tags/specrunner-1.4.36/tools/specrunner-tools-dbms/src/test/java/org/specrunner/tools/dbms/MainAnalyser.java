package org.specrunner.tools.dbms;

import org.specrunner.tools.dbms.BaseAnalyser;
import org.specrunner.tools.dbms.ConfigurationFiles;
import org.specrunner.tools.dbms.ConnectionInfo;

public class MainAnalyser {

    public static void main(String[] args) throws Exception {
        ConnectionInfo current = new ConnectionInfo("PUBLIC.DEP", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost/sql2", "sa", "");
        new BaseAnalyser().analyse(current, new ConfigurationFiles("/sr_dbms_tables.properties"), new ConfigurationFiles("/sr_dbms_columns.properties"));
    }
}