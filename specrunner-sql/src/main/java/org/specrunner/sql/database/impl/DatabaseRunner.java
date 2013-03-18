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
package org.specrunner.sql.database.impl;

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
import org.specrunner.sql.database.EMode;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.input.CommandType;
import org.specrunner.sql.input.INode;
import org.specrunner.sql.input.IRow;
import org.specrunner.sql.input.ITable;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.Value;
import org.specrunner.util.UtilLog;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;

/**
 * A database runner which uses prepared statements and meta-information to set
 * objects.
 * 
 * @author Thiago Santos
 * 
 */
public class DatabaseRunner implements IDatabase {

    /**
     * Prepared statements for input actions.
     */
    protected Map<String, PreparedStatement> inputs = new HashMap<String, PreparedStatement>();
    /**
     * Prepared statements for output actions.
     */
    protected Map<String, PreparedStatement> outputs = new HashMap<String, PreparedStatement>();

    @Override
    public void perform(Connection con, Schema schema, ITable data, EMode mode) {
        INode caption = data.caption();
        String tAlias = caption.getText();
        Table table = schema.getAlias(tAlias);
        if (table == null) {
            throw new RuntimeException("Table with alias '" + tAlias + "' not found in:" + schema.getAliasToTables().keySet());
        }

        List<INode> headers = data.headers();
        Column[] columns = new Column[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            String cAlias = headers.get(i).getText();
            columns[i] = table.getAlias(cAlias);
            if (i > 0 && columns[i] == null) {
                throw new RuntimeException("Column with alias '" + cAlias + "' not found in:" + table.getAliasToColumns().keySet());
            }
        }
        List<IRow> rows = data.rows();
        for (int i = 0; i < rows.size(); i++) {
            IRow row = rows.get(i);
            List<INode> tds = row.cells();
            if (tds.isEmpty()) {
                throw new RuntimeException("Empty lines are useless. " + row.getText());
            }
            String type = tds.get(0).getText();
            CommandType ct = CommandType.get(type);
            if (ct == null) {
                throw new RuntimeException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)");
            }
            Map<String, Value> filled = new HashMap<String, Value>();
            Set<Value> values = new TreeSet<Value>();
            for (int j = 1; j < tds.size(); j++) {
                Column c = columns[j];
                INode td = tds.get(j);
                String att = td.getAttribute("title");
                String value = att != null ? att : td.getText();

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
                            throw new RuntimeException(e);
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
                        throw new RuntimeException("Convertion error at row: " + i + ", cell: " + j + ". Attempt to convert '" + value + "' using a '" + converter + "'.", e);
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
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    Value v = new Value(c, obj, comparator);
                    values.add(v);
                    filled.put(c.getName(), v);
                }
            }
            switch (ct) {
            case INSERT:
                if (mode == EMode.INPUT) {
                    performInsert(con, table, values, filled);
                } else {
                    performSelect(con, table, values, filled, 1);
                }
                break;
            case UPDATE:
                if (mode == EMode.INPUT) {
                    performUpdate(con, table, values);
                } else {
                    performSelect(con, table, values, filled, 1);
                }
                break;
            case DELETE:
                if (mode == EMode.INPUT) {
                    performDelete(con, table, values);
                } else {
                    performSelect(con, table, values, filled, 0);
                }
                break;
            default:
                throw new RuntimeException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)");
            }
        }
        try {
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void performInsert(Connection con, Table table, Set<Value> values, Map<String, Value> filled) {
        for (Column c : table.getAliasToColumns().values()) {
            if (filled.get(c.getName()) == null && c.getDefaultValue() != null) {
                values.add(new Value(c, c.getDefaultValue(), c.getComparator()));
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
        performIn(con, sb.toString(), indexes, values, 1);
    }

    protected void performUpdate(Connection con, Table table, Set<Value> values) {
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
        performIn(con, sb.toString(), indexes, values, 1);
    }

    protected void performDelete(Connection con, Table table, Set<Value> values) {
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
        performIn(con, sb.toString(), indexes, values, 1);
    }

    protected void performIn(Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + indexes + ". values = " + values);
        }
        try {
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
                throw new RuntimeException("The expected update count (" + expectedCount + ") does not match, received = " + count + ".");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void performSelect(Connection con, Table table, Set<Value> values, Map<String, Value> filled, int expectedCount) {
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
        performOut(con, sb.toString(), indexes, values, expectedCount);
    }

    protected void performOut(Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + indexes + ". values = " + values + ". indexes = " + indexes);
        }
        try {
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
                        throw new RuntimeException("None register found with the given conditions: " + sql + "[" + values + "]");
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
                                throw new RuntimeException("Expected value '" + v.getValue() + "' does not match received '" + received + "'");
                            }
                        }
                    }
                    if (rs.next()) {
                        throw new RuntimeException("More than one register satisfy the condition: " + sql + "[" + values + "]");
                    }
                } else {
                    if (rs.next()) {
                        throw new RuntimeException("A result for " + sql + "[" + values + "] was not expected.");
                    }
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void release() {
        for (PreparedStatement ps : inputs.values()) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Release: " + ps);
            }
            try {
                if (!ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}