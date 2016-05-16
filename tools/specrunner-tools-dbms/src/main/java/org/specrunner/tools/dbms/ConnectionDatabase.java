/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.tools.dbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.utility.SchemaCrawlerUtility;

/**
 * Connection data.
 * 
 * @author Thiago Santos
 */
public class ConnectionDatabase {

    private Connection connection;
    private Catalog catalog;
    private DatabaseInfo database;

    public ConnectionDatabase(ConnectionInfo ci) throws Exception {
        SchemaCrawlerOptions options = new SchemaCrawlerOptions();
        prepareOptions(options, ci);
        Class.forName(ci.getDriver());
        connection = DriverManager.getConnection(ci.getUrl(), ci.getUser(), ci.getPassword());
        catalog = SchemaCrawlerUtility.getCatalog(connection, options);
        database = catalog.getDatabaseInfo();
    }

    @SuppressWarnings("serial")
    protected void prepareOptions(SchemaCrawlerOptions options, final ConnectionInfo ci) {
        options.setSchemaInclusionRule(new InclusionRule() {
            @Override
            public boolean test(String text) {
                return text.equalsIgnoreCase(ci.getSchema());
            }
        });
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
    }

    public Connection getConnection() {
        return connection;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public DatabaseInfo getDatabase() {
        return database;
    }

    @Override
    public void finalize() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
