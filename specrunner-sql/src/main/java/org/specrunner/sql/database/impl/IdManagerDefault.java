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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.converters.ConverterException;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.IIdManager;
import org.specrunner.sql.database.IStatementFactory;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.UtilLog;

/**
 * Manage tables and columns key fields.
 * 
 * @author Thiago Santos.
 * 
 */
public class IdManagerDefault implements IIdManager {

    /**
     * Mapping from values to IDs.
     */
    protected Map<String, Object> valuesToIds = new HashMap<String, Object>();
    /**
     * Local table name.
     */
    protected String table;
    /**
     * Local value.
     */
    protected String value;

    @Override
    public void reset() {
        valuesToIds.clear();
    }

    @Override
    public void clear() {
        table = null;
        value = null;
    }

    @Override
    public void append(String table, String value) {
        if (this.table == null) {
            this.table = table;
            this.value = value;
        } else {
            this.value += SEPARATOR + value;
        }
    }

    @Override
    public boolean hasKeys() {
        return table != null;
    }

    /**
     * Read generated keys.
     * 
     * @param pstmt
     *            The statement.
     * @throws SQLException
     *             On reading errors.
     */
    public void readKeys(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = null;
        try {
            rs = pstmt.getGeneratedKeys();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                for (int j = 1; j < metaData.getColumnCount() + 1; j++) {
                    bind(rs.getObject(j));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Bind value to current context information.
     * 
     * @param obj
     *            An object.
     */
    protected void bind(Object obj) {
        bind(makeKey(table, value), obj);
    }

    /**
     * Bind data to specific mappings.
     * 
     * @param value
     *            Value.
     * @param id
     *            Id value.
     */
    protected void bind(String value, Object id) {
        valuesToIds.put(value, id);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Added value -> id: " + value + " -> " + id);
        }
    }

    @Override
    public Object lookup(String table, String value) {
        String key = makeKey(table, value);
        Object obj = valuesToIds.get(key);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("'" + key + "' recovered as " + obj);
        }
        return obj;
    }

    /**
     * Create data key.
     * 
     * @param table
     *            Table name.
     * @param value
     *            Value.
     * @return A key.
     */
    protected String makeKey(String table, Object value) {
        return table + "." + value;
    }

    @Override
    public Object find(String tableOrAlias, String value, Column column, Connection connection, IStatementFactory statementFactory) throws DatabaseException, SQLException {
        String key = makeKey(tableOrAlias, value);
        Object result = lookup(tableOrAlias, value);
        if (result == null) {
            Schema schema = column.getParent().getParent();
            Table table = schema.getAlias(tableOrAlias);
            if (table == null) {
                throw new DatabaseException("Virtual column '" + tableOrAlias + "' not found in schema " + schema.getName() + ". It must be a name in domain set of: " + schema.getAliasToTables());
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Recover virtual key for (" + key + ") at " + schema.getName() + "." + table.getName());
            }
            StringTokenizer st = new StringTokenizer(String.valueOf(value), SEPARATOR);
            List<Column> references = table.getReferences();
            if (st.countTokens() != references.size()) {
                throw new DatabaseException("Number of reference columns (" + references.size() + ") is different from value tokens (" + st.countTokens() + ").");
            }
            PreparedStatement pstmt = statementFactory.getOutput(connection, createSelect(table), table);
            int i = 1;
            while (st.hasMoreTokens()) {
                Column reference = references.get(i - 1);
                String token = st.nextToken();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Convert(" + reference.getTableOrAlias() + "." + token + ")");
                }
                Object tmp = null;
                if (reference.isVirtual()) {
                    tmp = find(reference.getTableOrAlias(), token, reference, connection, statementFactory);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Virtual part(" + token + ") found:" + tmp + " ." + (tmp != null ? tmp.getClass() : "null"));
                    }
                } else {
                    try {
                        tmp = reference.getConverter().convert(token, reference.getArguments().toArray());
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("Converted to:" + tmp + " ." + (tmp != null ? tmp.getClass() : "null"));
                        }
                    } catch (ConverterException e) {
                        throw new DatabaseException(e);
                    }
                }
                if (reference.isDate()) {
                    IComparator comp = reference.getComparator();
                    if (!(comp instanceof ComparatorDate)) {
                        throw new DatabaseException("Date columns must have comparators of type 'date'. Current type:" + comp.getClass());
                    }
                    ComparatorDate comparator = (ComparatorDate) comp;
                    comparator.initialize();
                    Date dateBefore = new Date(((Date) tmp).getTime() - comparator.getTolerance());
                    Date dateAfter = new Date(((Date) tmp).getTime() + comparator.getTolerance());
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Date range in virtual lookup [" + dateBefore + " to " + dateAfter + "]");
                    }
                    pstmt.setObject(i++, dateBefore);
                    pstmt.setObject(i++, dateAfter);
                } else {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("findValue.SET(" + i + "," + reference.getTableOrAlias() + "," + reference.getName() + ") = " + tmp);
                    }
                    pstmt.setObject(i++, tmp);
                }
            }
            ResultSet rs = null;
            try {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Query(" + key + "): " + pstmt);
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    for (Column c : table.getKeys()) {
                        result = rs.getObject(c.getName());
                        bind(key, result);
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        for (Column c : references) {
                            UtilLog.LOG.debug("Value for " + c.getName() + ": " + rs.getObject(c.getName()));
                        }
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Virtual key (" + key + ") replaced by '" + result + "'");
        }
        return result;
    }

    /**
     * Create select SQL.
     * 
     * @param table
     *            A table.
     * @return A select SQL.
     */
    protected String createSelect(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        List<Column> keys = table.getKeys();
        int i = 0;
        for (Column c : keys) {
            sb.append((i++ == 0 ? "" : ",") + c.getName());
        }
        List<Column> references = table.getReferences();
        if (UtilLog.LOG.isDebugEnabled()) {
            for (Column c : references) {
                sb.append((i++ == 0 ? "" : ",") + c.getName());
            }
        }
        sb.append(" from ");
        sb.append(table.getParent().getName() + "." + table.getName());
        sb.append(" where ");
        i = 0;
        for (Column c : references) {
            sb.append((i++ == 0 ? "" : " AND ") + c.getName() + (c.isDate() ? " between ? and ?" : " = ?"));
        }
        return sb.toString();
    }
}
