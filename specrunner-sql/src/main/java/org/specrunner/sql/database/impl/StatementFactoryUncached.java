/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.sql.database.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.specrunner.plugins.PluginException;
import org.specrunner.sql.meta.Table;

/**
 * Default statement factory.
 * 
 * @author Thiago Santos.
 */
public class StatementFactoryUncached extends AbstractStatementFactory {

    @Override
    public PreparedStatement getInput(Connection connection, String sql, Table table) throws SQLException {
        return createInStatement(connection, sql, table);
    }

    @Override
    public void putInput(String sql, PreparedStatement pstmt) {
        // nothing to cache
    }

    @Override
    public PreparedStatement getOutput(Connection connection, String sql, Table table) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public void putOutput(String sql, PreparedStatement pstmt) {
        // nothing to cache
    }

    @Override
    public void release(PreparedStatement pstmt) throws SQLException {
        pstmt.close();
    }

    @Override
    public void release() throws PluginException {
        // nothing to release
    }
}
