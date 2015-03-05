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
package org.specrunner.sql.database.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.database.IStatementFactory;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Default statement factory.
 * 
 * @author Thiago Santos.
 */
public class StatementFactoryDefault implements IStatementFactory {

    /**
     * Prepared statements for input actions.
     */
    protected ICache<String, PreparedStatement> inputs = SRServices.get(ICacheFactory.class).newCache(StatementFactoryDefault.class.getName() + ".inputs", PreparedStatementCleaner.INSTANCE.get());

    /**
     * Prepared statements for output actions.
     */
    protected ICache<String, PreparedStatement> outputs = SRServices.get(ICacheFactory.class).newCache(StatementFactoryDefault.class.getName() + ".outputs", PreparedStatementCleaner.INSTANCE.get());

    @Override
    public PreparedStatement getInput(Connection connection, String sql, Table table) throws SQLException {
        PreparedStatement pstmt = inputs.get(sql);
        if (pstmt == null) {
            pstmt = createStatement(connection, sql, table);
            putInput(sql, pstmt);
        } else {
            pstmt.clearParameters();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("REUSE: " + pstmt);
            }
        }
        return pstmt;
    }

    /**
     * Create the prepared statement.
     * 
     * @param connection
     *            The connection.
     * @param sql
     *            A SQL.
     * @param table
     *            The table under analysis.
     * @return A new prepared statement.
     * @throws SQLException
     *             On creation errors.
     */
    protected PreparedStatement createStatement(Connection connection, String sql, Table table) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        if (meta.supportsGetGeneratedKeys()) {
            List<String> lista = new LinkedList<String>();
            for (Column c : table.getKeys()) {
                lista.add(c.getName());
            }
            return connection.prepareStatement(sql, lista.toArray(new String[lista.size()]));
        } else {
            return connection.prepareStatement(sql);
        }
    }

    @Override
    public void putInput(String sql, PreparedStatement pstmt) {
        inputs.put(sql, pstmt);
    }

    @Override
    public PreparedStatement getOutput(Connection connection, String sql, Table table) throws SQLException {
        PreparedStatement pstmt = outputs.get(sql);
        if (pstmt == null) {
            pstmt = connection.prepareStatement(sql);
            putOutput(sql, pstmt);
        } else {
            pstmt.clearParameters();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("REUSE:" + pstmt);
            }
        }
        return pstmt;
    }

    @Override
    public void putOutput(String sql, PreparedStatement pstmt) {
        outputs.put(sql, pstmt);
    }

    @Override
    public void release() throws PluginException {
        inputs.release();
        outputs.release();
    }
}