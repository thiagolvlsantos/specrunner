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
package org.specrunner.sql.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.specrunner.plugins.PluginException;
import org.specrunner.sql.meta.Table;

/**
 * Factory of prepared statements.
 * 
 * @author Thiago Santos
 */
public interface IStatementFactory {

    /**
     * Get input prepared statement.
     * 
     * @param connection
     *            Connection.
     * @param wrapper
     *            A wrapper.
     * @param table
     *            A table.
     * @return A new prepared statement, if not cached, otherwise, a reused one.
     * @throws SQLException
     *             On lookup errors.
     */
    PreparedStatement getInput(Connection connection, SqlWrapper wrapper, Table table) throws SQLException;

    /**
     * Get statement by SQL.
     * 
     * @param sql
     *            SQL.
     * @return A statement.
     */
    PreparedStatement getInput(String sql);

    /**
     * Add a statement to the factory.
     * 
     * @param sql
     *            SQL.
     * @param pstmt
     *            A statement.
     */
    void putInput(String sql, PreparedStatement pstmt);

    /**
     * Get output prepared statement.
     * 
     * @param connection
     *            Connection.
     * @param wrapper
     *            SQL wrapper.
     * @param table
     *            A table.
     * @return A new prepared statement, if not cached, otherwise a reused one.
     * @throws SQLException
     *             On lookup errors.
     */
    PreparedStatement getOutput(Connection connection, SqlWrapper wrapper, Table table) throws SQLException;

    /**
     * Get statement by SQL.
     * 
     * @param sql
     *            SQL.
     * @return A statement.
     */
    PreparedStatement getOutput(String sql);

    /**
     * Add a statement to the factory.
     * 
     * @param sql
     *            SQL.
     * @param pstmt
     *            A statement.
     */
    void putOutput(String sql, PreparedStatement pstmt);

    /**
     * Release database resources.
     * 
     * @throws PluginException
     *             On release errors.
     */
    void release() throws PluginException;
}