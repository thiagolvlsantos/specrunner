/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.dbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import schemacrawler.schema.Database;
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

    public Connection connnection;
    public Database database;

    @SuppressWarnings("serial")
    public ConnectionDatabase(final ConnectionInfo cd) throws Exception {
        SchemaCrawlerOptions options = new SchemaCrawlerOptions();
        options.setSchemaInclusionRule(new InclusionRule() {
            @Override
            public boolean include(String text) {
                return text.equalsIgnoreCase(cd.getSchema());
            }
        });
        options.setSchemaInfoLevel(SchemaInfoLevel.standard());
        Class.forName(cd.getDriver());
        connnection = DriverManager.getConnection(cd.getUrl(), cd.getUser(), cd.getPassword());
        database = SchemaCrawlerUtility.getDatabase(connnection, options);
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