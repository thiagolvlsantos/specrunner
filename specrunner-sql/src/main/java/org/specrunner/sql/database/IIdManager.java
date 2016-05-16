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
package org.specrunner.sql.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Table;

/**
 * An identity manager.
 * 
 * @author Thiago Santos
 */
public interface IIdManager {

    /**
     * Fields separator.
     */
    String SEPARATOR = ";";

    /**
     * Reset ID manager.
     */
    void reset();

    /**
     * Clear manager local variables.
     */
    void clear();

    /**
     * Add a table alias to processing.
     * 
     * @param table
     *            Table to append reference value.
     * @param value
     *            A reference value.
     */
    void append(String table, String value);

    /**
     * Check if manager has keys to read.
     * 
     * @return true, if reading is required, false, otherwise.
     */
    boolean hasKeys();

    /**
     * Read keys from a prepared statement.
     * 
     * @param wrapper
     *            A wrapper.
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param pstmt
     *            A statement.
     * 
     * @throws SQLException
     *             On reading errors.
     */
    void readKeys(SqlWrapper wrapper, Table table, IRegister register, PreparedStatement pstmt) throws SQLException;

    /**
     * Lookup for a ID based on a reference.
     * 
     * @param table
     *            Table alias to lookup.
     * @param value
     *            A object value to transform/recover.
     * @return ID corresponding to the object.
     */
    Object lookup(String table, String value);

    /**
     * Find a value by its table and alias, or by columns values.
     * 
     * @param table
     *            Table name.
     * @param value
     *            The value to be found.
     * @param column
     *            The reference column.
     * @param connection
     *            A connection.
     * @param statementFactory
     *            A statement factory.
     * @return The database object corresponding to the specified parameters, if
     *         found, null, otherwise.
     * @throws DatabaseException
     *             On reading errors.
     * @throws SQLException
     *             On select errors.
     */
    Object find(String table, String value, Column column, Connection connection, IStatementFactory statementFactory) throws DatabaseException, SQLException;
}
