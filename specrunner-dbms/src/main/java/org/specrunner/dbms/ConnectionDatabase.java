package org.specrunner.dbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

public class ConnectionDatabase {

    public Connection connnection;
    public Database database;

    private ConnectionDatabase(Connection connnection, Database database) {
        this.connnection = connnection;
        this.database = database;
    }

    @SuppressWarnings("serial")
    public static ConnectionDatabase newInstance(final ConnectionData cd) throws ClassNotFoundException, SQLException, SchemaCrawlerException {
        SchemaCrawlerOptions options = new SchemaCrawlerOptions();
        options.setSchemaInclusionRule(new InclusionRule() {
            @Override
            public boolean include(String text) {
                return text.equalsIgnoreCase(cd.getSchema());
            }
        });
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
        Class.forName(cd.getDriver());
        Connection connection = DriverManager.getConnection(cd.getUrl(), cd.getUser(), cd.getPassword());
        return new ConnectionDatabase(connection, SchemaCrawlerUtility.getDatabase(connection, options));
    }

    @Override
    public void finalize() {
        if (connnection != null) {
            try {
                connnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}