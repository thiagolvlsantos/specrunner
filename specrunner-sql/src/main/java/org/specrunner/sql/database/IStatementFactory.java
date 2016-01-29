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
     * Get statement by SQL.
     * 
     * @param connection
     *            Connection.
     * @param sql
     *            SQL.
     * @param table
     *            A table.
     * @return A statement.
     * @throws SQLException
     *             On lookup errors.
     */
    PreparedStatement getInput(Connection connection, String sql, Table table) throws SQLException;

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
     * Get statement by SQL.
     * 
     * @param connection
     *            Connection.
     * @param sql
     *            SQL.
     * @param table
     *            A table.
     * @return A statement.
     * @throws SQLException
     *             On lookup errors.
     */
    PreparedStatement getOutput(Connection connection, String sql, Table table) throws SQLException;

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
     * Release an individual statement.
     * 
     * @param pstmt
     *            The statement to be free.
     * @throws SQLException
     *             On release errors.
     */
    void release(PreparedStatement pstmt) throws SQLException;

    /**
     * Release database resources.
     * 
     * @throws PluginException
     *             On release errors.
     */
    void release() throws PluginException;
}
