package org.specrunner.sql.database.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.sql.database.IStatementFactory;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Table;

public abstract class AbstractStatementFactory implements IStatementFactory {

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
    protected PreparedStatement createInStatement(Connection connection, String sql, Table table) throws SQLException {
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

}