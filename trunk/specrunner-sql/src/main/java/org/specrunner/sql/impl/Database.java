/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.sql.CommandType;
import org.specrunner.sql.EMode;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.Value;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * Basic implementation of <code>IDatabase</code> using prepared statements.
 * 
 * @author Thiago Santos
 * 
 */
public class Database implements IDatabase {

    /**
     * Prepared statements for input actions.
     */
    protected Map<String, PreparedStatement> inputs = new HashMap<String, PreparedStatement>();
    /**
     * Prepared statements for output actions.
     */
    protected Map<String, PreparedStatement> outputs = new HashMap<String, PreparedStatement>();

    @Override
    public void perform(IPlugin plugin, IContext context, IResultSet result, TableAdapter tableAdapter, Connection con, Schema schema, EMode mode) throws PluginException {
        List<CellAdapter> captions = tableAdapter.getCaptions();
        if (captions.isEmpty()) {
            throw new PluginException("Tables must have a caption.");
        }
        String tAlias = captions.get(0).getValue();
        Table table = schema.getAlias(tAlias);
        if (table == null) {
            throw new PluginException("Table with alias '" + tAlias + "' not found in:" + schema.getAliasToTables().keySet());
        }
        List<RowAdapter> rows = tableAdapter.getRows();
        // headers are in the first row.
        RowAdapter header = rows.get(0);
        List<CellAdapter> headers = header.getCells();
        Column[] columns = new Column[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            String cAlias = headers.get(i).getValue();
            columns[i] = table.getAlias(cAlias);
            if (i > 0 && columns[i] == null) {
                throw new PluginException("Column with alias '" + cAlias + "' not found in:" + table.getAliasToColumns().keySet());
            }
        }
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter row = rows.get(i);
            List<CellAdapter> tds = row.getCells();
            if (tds.isEmpty()) {
                throw new PluginException("Empty lines are useless. " + row.getValue());
            }
            String type = tds.get(0).getValue();
            CommandType ct = CommandType.get(type);
            if (ct == null) {
                throw new PluginException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)");
            }
            Map<String, Value> filled = new HashMap<String, Value>();
            Set<Value> values = new TreeSet<Value>();
            for (int j = 1; j < tds.size(); j++) {
                Column c = columns[j];
                CellAdapter td = tds.get(j);
                String att = td.getAttribute("title");
                String value = att != null ? att : td.getValue();

                IConverter converter = c.getConverter();
                String strConverter = td.getAttribute("converter");
                if (strConverter != null) {
                    IConverterManager cm = SpecRunnerServices.get(IConverterManager.class);
                    IConverter instance = cm.get(converter);
                    if (instance == null) {
                        try {
                            converter = (IConverter) Class.forName(strConverter).newInstance();
                            cm.bind(strConverter, converter);
                        } catch (Exception e) {
                            throw new PluginException(e);
                        }
                    }
                }
                List<String> args = new LinkedList<String>();
                int index = 0;
                String arg = td.getAttribute("arg" + (index++));
                while (arg != null) {
                    args.add(arg);
                    arg = td.getAttribute("arg" + (index++));
                }
                if (converter.accept(value)) {
                    Object obj;
                    try {
                        obj = converter.convert(value, args.isEmpty() ? null : args.toArray());
                    } catch (ConverterException e) {
                        result.addResult(Failure.INSTANCE, context.newBlock(td.getElement(), plugin), "Convertion error at row: " + i + ", cell: " + j + ". Attempt to convert '" + value + "' using a '" + converter + "'.");
                        continue;
                    }
                    if (obj == null && ct == CommandType.INSERT) {
                        obj = c.getDefaultValue();
                    }
                    IComparator comparator = c.getComparator();
                    String strComparator = td.getAttribute("comparator");
                    if (strComparator != null) {
                        IComparatorManager cm = SpecRunnerServices.get(IComparatorManager.class);
                        IComparator instance = cm.get(converter);
                        if (instance == null) {
                            try {
                                comparator = (IComparator) Class.forName(strComparator).newInstance();
                                cm.bind(strComparator, comparator);
                            } catch (Exception e) {
                                throw new PluginException(e);
                            }
                        }
                    }
                    Value v = new Value(c, td, obj, comparator);
                    values.add(v);
                    filled.put(c.getName(), v);
                }
            }
            try {
                switch (ct) {
                case INSERT:
                    if (mode == EMode.INPUT) {
                        performInsert(context, result, con, table, values, filled);
                    } else {
                        performSelect(plugin, context, result, con, table, values, filled, 1);
                    }
                    break;
                case UPDATE:
                    if (mode == EMode.INPUT) {
                        performUpdate(context, result, con, table, values);
                    } else {
                        performSelect(plugin, context, result, con, table, values, filled, 1);
                    }
                    break;
                case DELETE:
                    if (mode == EMode.INPUT) {
                        performDelete(context, result, con, table, values);
                    } else {
                        performSelect(plugin, context, result, con, table, values, filled, 0);
                    }
                    break;
                default:
                    throw new PluginException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)");
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                try {
                    result.addResult(Failure.INSTANCE, context.newBlock(row.getElement(), plugin), new PluginException("Error in connection (" + con.getMetaData().getURL() + "): " + e.getMessage(), e));
                } catch (SQLException e1) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new PluginException("Could not log error:" + e1.getMessage(), e1);
                }
            }
        }
        try {
            con.commit();
        } catch (SQLException e) {
            throw new PluginException(e);
        }
    }

    /**
     * Perform database inserts.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param con
     *            The connection.
     * @param table
     *            The specification.
     * @param values
     *            The values.
     * @param filled
     *            Filled fields.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performInsert(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, Map<String, Value> filled) throws PluginException, SQLException {
        for (Column c : table.getAliasToColumns().values()) {
            if (filled.get(c.getName()) == null && c.getDefaultValue() != null) {
                values.add(new Value(c, null, c.getDefaultValue(), c.getComparator()));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + table.getSchema().getName() + "." + table.getName() + " (");
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value e : values) {
            indexes.put(e.getColumn().getName(), i++);
            sbVal.append(e.getColumn().getName() + ",");
            sbPla.append("?,");
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > 1) {
            sbPla.setLength(sbPla.length() - 1);
        }
        sb.append(sbVal);
        sb.append(") values (");
        sb.append(sbPla);
        sb.append(")");
        performIn(context, result, con, sb.toString(), indexes, values, 1);
    }

    /**
     * Perform database updates.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param con
     *            The connection.
     * @param table
     *            The specification.
     * @param values
     *            The values.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performUpdate(IContext context, IResultSet result, Connection con, Table table, Set<Value> values) throws PluginException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("update " + table.getSchema().getName() + "." + table.getName() + " set ");
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value v : values) {
            if (!v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbVal.append(v.getColumn().getName() + " = ?,");
            }
        }
        String and = " AND ";
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        sb.append(sbVal);
        sb.append(" where (");
        sb.append(sbPla);
        sb.append(")");
        performIn(context, result, con, sb.toString(), indexes, values, 1);
    }

    /**
     * Perform database deletes.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param con
     *            The connection.
     * @param table
     *            The specification.
     * @param values
     *            The values.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performDelete(IContext context, IResultSet result, Connection con, Table table, Set<Value> values) throws PluginException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from " + table.getSchema().getName() + "." + table.getName() + " where ");
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        String and = " AND ";
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        sb.append(sbPla);
        performIn(context, result, con, sb.toString(), indexes, values, 1);
    }

    /**
     * Perform database commands.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param con
     *            The connection.
     * @param sql
     *            The SQL.
     * @param indexes
     *            The column indexes.
     * @param values
     *            The values.
     * @param expectedCount
     *            Expected count to the operation.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performIn(IContext context, IResultSet result, Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) throws PluginException, SQLException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + indexes + ". values = " + values);
        }
        PreparedStatement pstmt = inputs.get(sql);
        if (pstmt == null) {
            pstmt = con.prepareStatement(sql);
            inputs.put(sql, pstmt);
        } else {
            pstmt.clearParameters();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("REUSE:" + pstmt);
            }
        }
        for (Value v : values) {
            Integer index = indexes.get(v.getColumn().getName());
            if (index != null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("SET(" + index + ")=" + v.getValue());
                }
                pstmt.setObject(index, v.getValue());
            }
        }
        int count = pstmt.executeUpdate();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("[" + count + "]=" + sql);
        }
        if (expectedCount != count) {
            throw new PluginException("The expected update count (" + expectedCount + ") does not match, received = " + count + ".");
        }
    }

    /**
     * Perform database select verifications.
     * 
     * @param plugin
     *            The source plugin.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param con
     *            The connection.
     * @param table
     *            The specification.
     * @param values
     *            The values.
     * @param filled
     *            Filled fields.
     * @param expectedCount
     *            The select expected count.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performSelect(IPlugin plugin, IContext context, IResultSet result, Connection con, Table table, Set<Value> values, Map<String, Value> filled, int expectedCount) throws PluginException, SQLException {
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value v : values) {
            if (!v.getColumn().isKey()) {
                sbVal.append(v.getColumn().getName() + ",");
            }
        }
        String and = " AND ";
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(sbVal);
        sb.append(" from " + table.getSchema().getName() + "." + table.getName());
        sb.append(" where ");
        sb.append(sbPla);
        performOut(plugin, context, result, con, sb.toString(), indexes, values, expectedCount);
    }

    /**
     * Perform database selects.
     * 
     * @param plugin
     *            The source plugin.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param con
     *            The connection.
     * @param sql
     *            The SQL.
     * @param indexes
     *            The column indexes.
     * @param values
     *            The values.
     * @param expectedCount
     *            Expected count to the operation.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performOut(IPlugin plugin, IContext context, IResultSet result, Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) throws PluginException, SQLException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + indexes + ". values = " + values + ". indexes = " + indexes);
        }
        PreparedStatement pstmt = inputs.get(sql);
        if (pstmt == null) {
            pstmt = con.prepareStatement(sql);
            inputs.put(sql, pstmt);
        } else {
            pstmt.clearParameters();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("REUSE:" + pstmt);
            }
        }
        for (Value v : values) {
            Integer index = indexes.get(v.getColumn().getName());
            if (index != null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("SET(" + index + ")=" + v.getValue());
                }
                pstmt.setObject(index, v.getValue());
            }
        }
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            if (expectedCount == 1) {
                if (!rs.next()) {
                    throw new PluginException("None register found with the given conditions: " + sql + "[" + values + "]");
                }
                for (Value v : values) {
                    Integer index = indexes.get(v.getColumn().getName());
                    if (index == null) {
                        IComparator comparator = v.getComparator();
                        Object received = rs.getObject(v.getColumn().getName());
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("CHECK(" + v.getValue() + ") = " + received);
                        }
                        if (!comparator.match(v.getValue(), received)) {
                            IStringAligner aligner = SpecRunnerServices.get(IStringAlignerFactory.class).align(String.valueOf(v.getValue()), String.valueOf(received));
                            result.addResult(Failure.INSTANCE, context.newBlock(v.getCell().getElement(), plugin), new DefaultAlignmentException(aligner));
                        }
                    }
                }
                if (rs.next()) {
                    throw new PluginException("More than one register satisfy the condition: " + sql + "[" + values + "]");
                }
            } else {
                if (rs.next()) {
                    throw new PluginException("A result for " + sql + "[" + values + "] was not expected.");
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public void release() throws PluginException {
        for (PreparedStatement ps : inputs.values()) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Release: " + ps);
            }
            try {
                if (!ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new PluginException(e);
            }
        }
    }
}
