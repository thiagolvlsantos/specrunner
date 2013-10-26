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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import nu.xom.Element;

import org.specrunner.SpecRunnerServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.impl.ComparatorDate;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.CommandType;
import org.specrunner.sql.EMode;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.UtilNames;
import org.specrunner.sql.meta.Value;
import org.specrunner.sql.meta.impl.UtilSchema;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * Basic implementation of <code>IDatabase</code> using prepared statements.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class Database implements IDatabase {

    /**
     * Feature for database error dump limit.
     */
    public static final String FEATURE_LIMIT = Database.class.getName() + ".limit";

    /**
     * Prepared statements for input actions.
     */
    protected Map<String, PreparedStatement> inputs = new HashMap<String, PreparedStatement>();

    /**
     * Map of named registers in database. When a resource is included and
     * generate keys, these keys are hold in the format:
     * 
     * <pre>
     * &lt;Table alias&gt;.&ltreference fields separated by ';'&gt; -> generated ID
     * </pre>
     * 
     * in this table.
     */
    protected Map<String, Object> namesToKeys = new HashMap<String, Object>();
    /**
     * Record the opposite side of generated IDs mappings. This is used to
     * provide better error messages in comparison failures. Its format is:
     * 
     * <pre>
     * &lt;Table alias&gt;.&ltgenerated ID&gt; -> reference fields separated by ';'
     * </pre>
     */
    protected Map<String, Object> keysToNames = new HashMap<String, Object>();

    /**
     * Prepared statements for output actions.
     */
    protected Map<String, PreparedStatement> outputs = new HashMap<String, PreparedStatement>();

    /**
     * Separator of virtual keys.
     */
    private static final String VIRTUAL_SEPARATOR = ";";

    /**
     * Feature for dump size.
     */
    private static final Integer DEFAULT_LIMIT = 100;

    /**
     * Max size of errors dump.
     */
    private Integer limit = DEFAULT_LIMIT;

    /**
     * Get the error dump limit.
     * 
     * @return The limit.
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Set error limit.
     * 
     * @param limit
     *            The limit.
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public void initialize() {
        SpecRunnerServices.getFeatureManager().set(FEATURE_LIMIT, this);
        // every use of plugin database clear mappings to avoid memory overload
        // and test interference
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Cleanning map of virtual names to IDs. Size before clean: " + (namesToKeys.size()));
        }
        namesToKeys.clear();
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Cleanning map of IDs to virtual names. Size before clean: " + (keysToNames.size()));
        }
        keysToNames.clear();
    }

    @Override
    public void perform(IContext context, IResultSet result, TableAdapter tableAdapter, Connection con, Schema schema, EMode mode) throws PluginException {
        List<CellAdapter> captions = tableAdapter.getCaptions();
        if (captions.isEmpty()) {
            throw new PluginException("Tables must have a caption.");
        }
        String tAlias = captions.get(0).getValue();
        Table table = schema.getAlias(tAlias);
        if (table == null) {
            throw new PluginException("Table '" + UtilNames.normalize(tAlias) + "' not found in schema " + schema.getAlias() + "(" + schema.getName() + "), avaliable tables alias: " + schema.getAliasToTables().keySet());
        }
        // creates a copy only of defined tables
        table = table.copy();
        List<RowAdapter> rows = tableAdapter.getRows();
        // headers are in the first row.
        RowAdapter header = rows.get(0);
        List<CellAdapter> headers = header.getCells();
        Column[] columns = new Column[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            CellAdapter cell = headers.get(i);
            String cAlias = cell.getValue();
            columns[i] = table.getAlias(cAlias);
            Column column = columns[i];
            if (i > 0 && column == null) {
                throw new PluginException("Column with alias '" + cAlias + "' not found in:" + table.getAliasToColumns().keySet());
            }
            // update to specific header adjusts
            if (column != null) {
                try {
                    UtilSchema.setupColumn(column, cell);
                } catch (ConverterException e) {
                    throw new PluginException(e);
                } catch (ComparatorException e) {
                    throw new PluginException(e);
                }
            }
        }
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter row = rows.get(i);
            List<CellAdapter> tds = row.getCells();
            if (tds.isEmpty()) {
                throw new PluginException("Empty lines are useless. " + row.getValue());
            }
            if (tds.size() != headers.size()) {
                throw new PluginException("Invalid number of cells at row: " + i + ". Expected " + headers.size() + " columns, received " + tds.size() + ".\n\t ROW:" + row);
            }
            int expectedCount = Integer.parseInt(row.getAttribute("count", "1"));
            String type = tds.get(0).getValue();
            CommandType ct = CommandType.get(type);
            if (ct == null) {
                throw new PluginException("Invalid command type. '" + type + "' at (row: " + i + ", cell: 0)");
            }
            Map<String, Value> filled = new HashMap<String, Value>();
            Set<Value> values = new TreeSet<Value>();
            for (int j = 1; j < tds.size(); j++) {
                Column column = columns[j].copy();
                CellAdapter td = tds.get(j);
                try {
                    UtilSchema.setupColumn(column, td);
                } catch (ConverterException e) {
                    throw new PluginException(e);
                } catch (ComparatorException e) {
                    throw new PluginException(e);
                }
                String value = UtilEvaluator.replace(td.getValue(), context, true);
                IConverter converter = column.getConverter();
                if (column.isVirtual() || converter.accept(value)) {
                    Object obj = null;
                    if (column.isVirtual()) {
                        obj = value;
                    } else {
                        List<String> args = td.getArguments();
                        if (args.isEmpty()) {
                            args = column.getArguments();
                        }
                        try {
                            obj = converter.convert(value, args.isEmpty() ? null : args.toArray());
                        } catch (ConverterException e) {
                            result.addResult(Failure.INSTANCE, context.newBlock(td.getNode(), context.getPlugin()), new PluginException("Convertion error at row: " + i + ", cell: " + j + ". Attempt to convert '" + value + "' using a '" + converter + "'."));
                            continue;
                        }
                    }
                    if (obj == null && ct == CommandType.INSERT) {
                        obj = column.getDefaultValue();
                    }
                    Value v = new Value(column, td, obj, column.getComparator());
                    values.add(v);
                    filled.put(column.getName(), v);
                }
            }
            try {
                boolean error = false;
                switch (ct) {
                case INSERT:
                    if (mode == EMode.INPUT) {
                        performInsert(context, result, con, table, values, filled);
                    } else {
                        performSelect(context, result, con, table, values, filled, 1);
                    }
                    break;
                case UPDATE:
                    if (mode == EMode.INPUT) {
                        performUpdate(context, result, con, table, values, expectedCount);
                    } else {
                        performSelect(context, result, con, table, values, filled, 1);
                    }
                    break;
                case DELETE:
                    if (mode == EMode.INPUT) {
                        performDelete(context, result, con, table, values, expectedCount);
                    } else {
                        performSelect(context, result, con, table, values, filled, 0);
                    }
                    break;
                default:
                    result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), context.getPlugin()), new PluginException("Invalid command type. '" + type + "' at (row:" + i + ", cell:0)"));
                    error = true;
                }
                if (!error) {
                    result.addResult(Success.INSTANCE, context.newBlock(row.getNode(), context.getPlugin()));
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                try {
                    result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), context.getPlugin()), new PluginException("Error in connection (" + con.getMetaData().getURL() + "): " + e.getMessage(), e));
                } catch (SQLException e1) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new PluginException("Could not log error:" + e1.getMessage(), e1);
                }
            } catch (PluginException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), context.getPlugin()), e);
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
            if (filled.get(c.getName()) == null && c.isSequence()) {
                String sql = ("NEXT VALUE FOR {0}").replace("{0}", c.getSequence());
                Element td = new Element("td");
                td.appendChild(String.valueOf(sql));
                values.add(new Value(c, new CellAdapter(td), sql, c.getComparator()));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + table.getSchema().getName() + "." + table.getName() + " (");
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        int i = 1;
        for (Value e : values) {
            sbVal.append(e.getColumn().getName() + ",");
            if (e.getColumn().getSequence() != null) {
                sbPla.append(e.getValue() + ",");
            } else {
                sbPla.append("?,");
                indexes.put(e.getColumn().getName(), i++);
            }
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
        performIn(context, result, con, insertWrapper(sb), table, indexes, values);
    }

    /**
     * Creates an insert wrapper.
     * 
     * @param sb
     *            The command.
     * @return A wrapper.
     */
    protected SqlWrapper insertWrapper(StringBuilder sb) {
        return SqlWrapper.insert(sb.toString(), 1);
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
     * @param expectedCount
     *            The expected action counter.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performUpdate(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, int expectedCount) throws PluginException, SQLException {
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
        performIn(context, result, con, updateWrapper(sb, expectedCount), table, indexes, values);
    }

    /**
     * Creates an update wrapper.
     * 
     * @param sb
     *            The command.
     * @param expectedCount
     *            The command expected counter.
     * @return A wrapper.
     */
    protected SqlWrapper updateWrapper(StringBuilder sb, int expectedCount) {
        return SqlWrapper.update(sb.toString(), expectedCount);
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
     * @param expectedCount
     *            The delete expected count.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performDelete(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, int expectedCount) throws PluginException, SQLException {
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
        // if keys are not present
        if (sbPla.length() == 0) {
            i = 1;
            for (Value v : values) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        sb.append(sbPla);
        performIn(context, result, con, SqlWrapper.delete(sb.toString(), expectedCount), table, indexes, values);
    }

    /**
     * Creates a delete wrapper.
     * 
     * @param sb
     *            The command.
     * @param expectedCount
     *            The command expected counter.
     * @return A wrapper.
     */
    protected SqlWrapper deleteWrapper(StringBuilder sb, int expectedCount) {
        return SqlWrapper.delete(sb.toString(), expectedCount);
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
     * @param sqlWrapper
     *            The SQL wrapper.
     * @param table
     *            The target table.
     * @param indexes
     *            The column indexes.
     * @param values
     *            The values.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performIn(IContext context, IResultSet result, Connection con, SqlWrapper sqlWrapper, Table table, Map<String, Integer> indexes, Set<Value> values) throws PluginException, SQLException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sqlWrapper.getSql() + ". MAP:" + indexes + ". values = " + values);
        }
        DatabaseMetaData meta = con.getMetaData();
        boolean generatedKeys = meta.supportsGetGeneratedKeys();
        PreparedStatement pstmt = inputs.get(sqlWrapper.getSql());
        if (pstmt == null) {
            if (generatedKeys) {
                List<String> lista = new LinkedList<String>();
                for (Column c : table.getKeys()) {
                    lista.add(c.getName());
                }
                pstmt = con.prepareStatement(sqlWrapper.getSql(), lista.toArray(new String[lista.size()]));
            } else {
                pstmt = con.prepareStatement(sqlWrapper.getSql());
            }
            inputs.put(sqlWrapper.getSql(), pstmt);
        } else {
            pstmt.clearParameters();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("REUSE:" + pstmt);
            }
        }
        String keyToName = null;
        String nameToKey = null;
        for (Value v : values) {
            Column column = v.getColumn();
            Integer index = indexes.get(column.getName());
            if (index != null) {
                Object obj = v.getValue();
                if (column.isVirtual()) {
                    String key = column.getAlias() + "." + obj;
                    obj = namesToKeys.get(key);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Virtual value '" + key + "' replaced by " + obj);
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("SET(" + index + ")=" + obj);
                }
                pstmt.setObject(index, obj);
                if (column.isReference()) {
                    CellAdapter cell = v.getCell();
                    String str = UtilEvaluator.replace(cell.getValue(), context, true);
                    if (keyToName == null) {
                        String alias = column.getTable().getAlias();
                        keyToName = alias + "." + str;
                        nameToKey = alias + ".{key}";
                    } else {
                        keyToName += VIRTUAL_SEPARATOR + str;
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Column reference '" + keyToName + "'.");
                    }
                }
            }
        }
        int count = pstmt.executeUpdate();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("[" + count + "]=" + sqlWrapper.getSql());
        }
        if (generatedKeys && keyToName != null) {
            ResultSet rs = null;
            try {
                rs = pstmt.getGeneratedKeys();
                ResultSetMetaData metaData = rs.getMetaData();
                boolean fromGenerated = false;
                while (rs.next()) {
                    fromGenerated = true;
                    for (int j = 1; j < metaData.getColumnCount() + 1; j++) {
                        bind(keyToName, nameToKey, rs.getObject(j));
                    }
                }
                if (!fromGenerated) {
                    // it came from a sequence
                    for (Value v : values) {
                        if (v.getColumn().isSequence()) {
                            bind(keyToName, nameToKey, v.getValue());
                            break;
                        }
                    }
                }
                if (sqlWrapper.getType() == CommandType.DELETE) {
                    Object object = namesToKeys.get(keyToName);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Removed item (" + keyToName + " -> " + object + ")");
                    }
                    namesToKeys.remove(keyToName);
                    keysToNames.remove(nameToKey.replace("{key}", String.valueOf(object)));
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        if (sqlWrapper.getExpectedCount() != count) {
            throw new PluginException("The expected update count (" + sqlWrapper.getExpectedCount() + ") does not match, received = " + count + ".\n\tSQL: " + sqlWrapper.getSql() + "\n\tARGS: " + values);
        }
    }

    /**
     * Bind names to values and values to names.
     * 
     * @param nameToKey
     *            The object name key.
     * @param keyToName
     *            The object value key.
     * @param generated
     *            The object to map.
     */
    protected void bind(String nameToKey, String keyToName, Object generated) {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Save item (" + nameToKey + " -> " + generated + ")");
        }
        namesToKeys.put(nameToKey, generated);
        keysToNames.put(keyToName.replace("{key}", String.valueOf(generated)), nameToKey.substring(nameToKey.indexOf('.') + 1));
    }

    /**
     * Perform database select verifications.
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
     * @param expectedCount
     *            The select expected count.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performSelect(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, Map<String, Value> filled, int expectedCount) throws PluginException, SQLException {
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        boolean hasKeys = false;
        for (Value v : values) {
            if (v.getColumn().isKey()) {
                hasKeys = true;
                break;
            }
        }
        String and = " AND ";
        int i = 1;
        if (hasKeys) {
            for (Value v : values) {
                if (!v.getColumn().isKey()) {
                    sbVal.append(v.getColumn().getName() + ",");
                }
            }
            for (Value v : values) {
                if (v.getColumn().isKey()) {
                    indexes.put(v.getColumn().getName(), i++);
                    sbPla.append(v.getColumn().getName() + " = ?" + and);
                }
            }
        } else {
            for (Value v : values) {
                sbVal.append(v.getColumn().getName() + ",");
            }
            for (Value v : values) {
                indexes.put(v.getColumn().getName(), i++);
                sbPla.append(v.getColumn().getName() + " = ?" + and);
            }
        }
        if (sbVal.length() == 0) {
            // when only keys are provided
            for (Value v : values) {
                sbVal.append(v.getColumn().getName() + ",");
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
        performOut(context, result, con, sb.toString(), indexes, values, expectedCount);
    }

    /**
     * Perform database selects.
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
    protected void performOut(IContext context, IResultSet result, Connection con, String sql, Map<String, Integer> indexes, Set<Value> values, int expectedCount) throws PluginException, SQLException {
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
            Column column = v.getColumn();
            Integer index = indexes.get(column.getName());
            if (index != null) {
                Object value = v.getValue();
                if (column.isVirtual()) {
                    value = findValue(con, column, value);
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("SET(" + index + ")=" + value);
                }
                pstmt.setObject(index, value);
                v.setValue(value);
            }
        }
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            if (expectedCount == 1) {
                if (!rs.next()) {
                    throw new PluginException("None register found with the given conditions: " + sql + " and values: [" + values + "]");
                }
                for (Value v : values) {
                    Column column = v.getColumn();
                    Integer index = indexes.get(column.getName());
                    if (index == null) {
                        IComparator comparator = v.getComparator();
                        Object received = rs.getObject(column.getName());
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("CHECK(" + v.getValue() + ") = " + received);
                        }
                        Object value = v.getValue();
                        if (column.isVirtual()) {
                            value = findValue(con, column, value);
                        }
                        comparator.initialize();
                        if (!comparator.match(value, received)) {
                            Object expected = v.getValue();
                            if (column.isVirtual()) {
                                received = keysToNames.get(column.getAlias() + "." + received);
                            }
                            result.addResult(Failure.INSTANCE, context.newBlock(v.getCell().getNode(), context.getPlugin()), new DefaultAlignmentException(String.valueOf(expected), String.valueOf(received)));
                        }
                    }
                }
                if (rs.next()) {
                    throw new PluginException("More than one register satisfy the condition: " + sql + "[" + values + "]\n" + dumpRs("Extra itens:", rs));
                }
            } else {
                if (rs.next()) {
                    throw new PluginException("A result for " + sql + "[" + values + "] was not expected.\n" + dumpRs("Unxepected itens:", rs));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Find a value by its virtual reference.
     * 
     * @param con
     *            The connection.
     * @param column
     *            The value column.
     * @param value
     *            The current value.
     * @return The object to set in outer select.
     * @throws SQLException
     *             On SQL errors.
     * @throws PluginException
     *             On execution errors.
     */
    protected Object findValue(Connection con, Column column, Object value) throws SQLException, PluginException {
        String key = column.getAlias() + "." + value;
        Object result = namesToKeys.get(key);
        if (result == null) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Recover virtual key for (" + key + ")");
            }
            Schema schema = column.getTable().getSchema();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Lookup in schema " + schema.getName());
            }
            Table table = schema.getAlias(column.getAlias());
            if (table == null) {
                throw new PluginException("Virtual column '" + column.getAlias() + "' not found in schema " + schema.getName() + ". It must be a name in domain set of: " + schema.getAliasToTables());
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Lookup in table " + table.getName());
            }
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
            sb.append(schema.getName() + "." + table.getName());
            sb.append(" where ");
            i = 0;
            for (Column c : references) {
                sb.append((i++ == 0 ? "" : " AND ") + c.getName() + (c.isDate() ? " between ? and ?" : " = ?"));
            }
            String sql = sb.toString();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Query for (" + value + "):" + sql);
            }
            PreparedStatement inPstmt = outputs.get(sql);
            if (inPstmt == null) {
                inPstmt = con.prepareStatement(sql);
                outputs.put(sql, inPstmt);
            } else {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("From cache:" + inPstmt);
                }
            }
            StringTokenizer st = new StringTokenizer(String.valueOf(value), VIRTUAL_SEPARATOR);
            i = 1;
            while (st.hasMoreTokens()) {
                Column reference = references.get(i - 1);
                String token = st.nextToken();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Convert(" + reference.getAlias() + "." + token + ")");
                }
                Object tmp = null;
                if (reference.isVirtual()) {
                    tmp = findValue(con, reference, token);
                } else {
                    try {
                        tmp = reference.getConverter().convert(token, reference.getArguments().toArray());
                    } catch (ConverterException e) {
                        throw new PluginException(e);
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Converted to:" + tmp + " ." + (tmp != null ? tmp.getClass() : "null"));
                }
                if (reference.isDate()) {
                    IComparator comp = reference.getComparator();
                    if (!(comp instanceof ComparatorDate)) {
                        throw new PluginException("Date columns must have comparators of type 'date'. Current type:" + comp.getClass());
                    }
                    ComparatorDate comparator = (ComparatorDate) comp;
                    comparator.initialize();
                    Date dateBefore = new Date(((Date) tmp).getTime() - comparator.getTolerance());
                    Date dateAfter = new Date(((Date) tmp).getTime() + comparator.getTolerance());
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Date range in virtual lookup [" + dateBefore + " to " + dateAfter + "]");
                    }
                    inPstmt.setObject(i++, dateBefore);
                    inPstmt.setObject(i++, dateAfter);
                } else {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("SET(" + i + ")=" + tmp);
                    }
                    inPstmt.setObject(i++, tmp);
                }
            }
            ResultSet rs = null;
            try {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Query: " + inPstmt);
                }
                rs = inPstmt.executeQuery();
                while (rs.next()) {
                    for (Column c : keys) {
                        result = rs.getObject(c.getName());
                        namesToKeys.put(key, result);
                        String inverse = c.getTable().getAlias() + "." + result;
                        keysToNames.put(inverse, value);

                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Add name -> id: " + key + " -> " + result);
                            UtilLog.LOG.info("Add id -> name: " + inverse + " -> " + value);
                        }
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
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Virtual key (" + key + ") replaced by '" + result + "'");
        }
        return result;
    }

    /**
     * Dump result set.
     * 
     * @param prefix
     *            The message prefix.
     * @param rs
     *            The result set.
     * @return A string for result set.
     * @throws SQLException
     *             On reading errors.
     */
    protected String dumpRs(String prefix, ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder(prefix);
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        int index = 0;
        do {
            sb.append("\n");
            for (int i = 1; i <= count; i++) {
                sb.append((i == 1 ? "\t" : ", ") + meta.getColumnName(i) + ":" + rs.getObject(i));
            }
            index++;
        } while (index < limit && rs.next());
        return sb.toString();
    }

    @Override
    public void release() throws PluginException {
        StringBuilder sb = new StringBuilder();
        release(sb, inputs.values());
        release(sb, outputs.values());
        if (sb.length() != 0) {
            throw new PluginException(sb.toString());
        }
    }

    /**
     * Close a set of prepared statements.
     * 
     * @param sb
     *            The error log.
     * @param pstms
     *            The collection of prepared statements.
     */
    protected void release(StringBuilder sb, Collection<PreparedStatement> pstms) {
        for (PreparedStatement ps : pstms) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Release: " + ps);
            }
            try {
                if (!ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                sb.append(e.getMessage());
            }
        }
    }
}