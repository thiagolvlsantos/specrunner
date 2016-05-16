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
package org.specrunner.sql.database.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.specrunner.expressions.EMode;
import org.specrunner.sql.database.CommandType;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.DatabaseRegisterEvent;
import org.specrunner.sql.database.DatabaseTableEvent;
import org.specrunner.sql.database.IDatabaseListener;
import org.specrunner.sql.meta.Table;

/**
 * Database analyzer based on operations counting.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class DatabaseCountListener implements IDatabaseListener {

    /**
     * Counter of registers per table in.
     */
    private Map<String, Integer> countersIn = new HashMap<String, Integer>();

    /**
     * Counter of registers per table out.
     */
    private Map<String, Integer> countersOut = new HashMap<String, Integer>();

    @Override
    public void initialize() {
    }

    @Override
    public void onTableIn(DatabaseTableEvent event) throws DatabaseException {
    }

    @Override
    public void onRegisterIn(DatabaseRegisterEvent event) {
        String name = event.getTable().getName();
        CommandType type = event.getWrapper().getType();
        int count = event.getWrapper().getExpectedCount();

        Integer old = countersIn.get(name);
        if (old == null) {
            old = 0;
        }
        if (CommandType.INSERT == type) {
            old += count;
        } else if (CommandType.DELETE == type) {
            if (count == Integer.MAX_VALUE) {
                old = 0;
            } else {
                old -= count;
            }
        }
        countersIn.put(name, old);
    }

    @Override
    public void onRegisterOut(DatabaseRegisterEvent event) {
        String name = event.getTable().getName();
        CommandType type = event.getWrapper().getType();
        int count = CommandType.DELETE == type ? 1 : event.getWrapper().getExpectedCount();

        Integer old = countersOut.get(name);
        if (old == null) {
            old = 0;
        }
        if (CommandType.INSERT == type) {
            old += count;
        } else if (CommandType.DELETE == type) {
            old -= count;
        }
        countersOut.put(name, old);
    }

    @Override
    public void onTableOut(DatabaseTableEvent event) throws DatabaseException {
        if (event.getMode() == EMode.OUTPUT) {
            String name = event.getTable().getName();

            Integer in = countersIn.get(name);
            if (in == null) {
                in = 0;
            }
            Integer out = countersOut.get(name);
            if (out == null) {
                out = 0;
            }
            checkCount(event.getConnection(), event.getTable(), in + out);
        }
    }

    /**
     * Check if the given table count match the number of expected items.
     * 
     * @param con
     *            A connection.
     * @param table
     *            A table meta-data.
     * @param rows
     *            Rows to check.
     * @throws DatabaseException
     *             On count errors.
     */
    protected void checkCount(Connection con, Table table, int rows) throws DatabaseException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            String alias = table.getParent().getName() + "." + table.getName();
            rs = stmt.executeQuery("select count(*) from " + alias);
            if (rs.next()) {
                int received = rs.getInt(1);
                int expected = rows;
                if (received != expected) {
                    throw new DatabaseException("The number of elements present in table '" + alias + "' is " + received + ", expected was " + expected);
                }
            } else {
                throw new DatabaseException("Could not count rows for table '" + alias + "'.");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
        }
    }
}
