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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.specrunner.SpecRunnerServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.CommandType;
import org.specrunner.sql.EMode;
import org.specrunner.sql.IDatabase;
import org.specrunner.sql.ISequenceProvider;
import org.specrunner.sql.SqlWrapper;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.UtilNames;
import org.specrunner.sql.meta.Value;
import org.specrunner.sql.meta.impl.UtilSchema;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;

/**
 * Basic implementation of <code>IDatabase</code> using cached prepared
 * statements, an ID manager to work with generated keys, and a sequence
 * provider to enable sequence interactions.
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
     * Feature for object manager instance.
     */
    public static final String FEATURE_ID_MANAGER = Database.class.getName() + ".idManager";

    /**
     * Feature for sequence provider instance.
     */
    public static final String FEATURE_SEQUENCE_PROVIDER = Database.class.getName() + ".sequenceProvider";

    /**
     * Prepared statements for input actions.
     */
    protected ICache<String, PreparedStatement> inputs = SpecRunnerServices.get(ICacheFactory.class).newCache(Database.class.getName() + ".inputs", PreparedStatementCleaner.INSTANCE.get());

    /**
     * Prepared statements for output actions.
     */
    protected ICache<String, PreparedStatement> outputs = SpecRunnerServices.get(ICacheFactory.class).newCache(Database.class.getName() + ".outputs", PreparedStatementCleaner.INSTANCE.get());

    /**
     * Manage object lookup and reuse.
     */
    protected IdManager idManager = new IdManager();

    /**
     * Sequence next value generator.
     */
    protected ISequenceProvider sequenceProvider = new SequenceProviderImpl();

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

    /**
     * Get the id manager.
     * 
     * @return The manager.
     */
    public IdManager getIdManager() {
        return idManager;
    }

    /**
     * Set the manager.
     * 
     * @param idManager
     *            The manager.
     */
    public void setIdManager(IdManager idManager) {
        this.idManager = idManager;
    }

    /**
     * Get the sequence values provider.
     * 
     * @return The provider.
     */
    public ISequenceProvider getSequenceProvider() {
        return sequenceProvider;
    }

    /**
     * Set the sequence provider.
     * 
     * @param sequenceProvider
     *            The provider.
     */
    public void setSequenceProvider(ISequenceProvider sequenceProvider) {
        this.sequenceProvider = sequenceProvider;
    }

    @Override
    public void initialize() {
        IFeatureManager fm = SpecRunnerServices.getFeatureManager();
        fm.set(FEATURE_LIMIT, this);
        fm.set(FEATURE_ID_MANAGER, this);
        fm.set(FEATURE_SEQUENCE_PROVIDER, this);
        // every use of database clear mappings to avoid memory overload and
        // test interference
        idManager.clear();
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
                        // the other column fields with default value are set in
                        // <code>addMissingValues(...)</code> method.
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
                        performSelect(context, result, con, table, values, 1);
                    }
                    break;
                case UPDATE:
                    if (mode == EMode.INPUT) {
                        performUpdate(context, result, con, table, values, expectedCount);
                    } else {
                        performSelect(context, result, con, table, values, 1);
                    }
                    break;
                case DELETE:
                    if (mode == EMode.INPUT) {
                        performDelete(context, result, con, table, values, expectedCount);
                    } else {
                        performSelect(context, result, con, table, values, 0);
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
        Map<String, Integer> indexes = new HashMap<String, Integer>();

        addMissingValues(table, filled, values);

        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + table.getSchema().getName() + "." + table.getName() + " (");
        StringBuilder sbColumns = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        int i = 1;
        for (Value e : values) {
            Column column = e.getColumn();
            sbColumns.append(column.getName() + ",");
            if (column.isSequence()) {
                sbValues.append(e.getValue() + ",");
            } else {
                sbValues.append("?,");
                indexes.put(column.getName(), i++);
            }
        }
        if (sbColumns.length() > 1) {
            sbColumns.setLength(sbColumns.length() - 1);
        }
        if (sbValues.length() > 1) {
            sbValues.setLength(sbValues.length() - 1);
        }
        sb.append(sbColumns);
        sb.append(") values (");
        sb.append(sbValues);
        sb.append(")");

        performIn(context, result, con, insertWrapper(sb), table, indexes, values);
    }

    /**
     * Add missing values to insert value set.
     * 
     * @param table
     *            The table.
     * @param filled
     *            A map of filled fields.
     * @param values
     *            The set of values.
     */
    protected void addMissingValues(Table table, Map<String, Value> filled, Set<Value> values) {
        for (Column column : table.getColumns()) {
            // table columns not present in test table
            if (filled.get(column.getName()) == null) {
                if (column.getDefaultValue() != null) {
                    // with default values should be set
                    values.add(new Value(column, null, column.getDefaultValue(), column.getComparator()));
                } else if (column.isSequence()) {
                    // or if it is a sequence: add their next value command.
                    values.add(new Value(column, null, sequenceProvider.nextValue(column.getSequence()), column.getComparator()));
                }
            }
        }
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
        Map<String, Integer> indexes = new HashMap<String, Integer>();

        StringBuilder sb = new StringBuilder();
        sb.append("update " + table.getSchema().getName() + "." + table.getName() + " set ");

        StringBuilder sbColumns = new StringBuilder();
        int i = 1;
        for (Value v : values) {
            Column column = v.getColumn();
            if (!column.isKey()) {
                indexes.put(column.getName(), i++);
                sbColumns.append(column.getName() + " = ?,");
            }
        }
        if (sbColumns.length() > 1) {
            sbColumns.setLength(sbColumns.length() - 1);
        }
        sb.append(sbColumns);

        String and = " AND ";

        sb.append(" where (");
        StringBuilder sbConditions = new StringBuilder();
        for (Value v : values) {
            Column column = v.getColumn();
            if (column.isKey()) {
                indexes.put(column.getName(), i++);
                sbConditions.append(column.getName() + " = ? " + and);
            }
        }
        if (sbConditions.length() > (1 + and.length())) {
            sbConditions.setLength(sbConditions.length() - (1 + and.length()));
        }
        sb.append(sbConditions);
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
        Map<String, Integer> indexes = new HashMap<String, Integer>();

        StringBuilder sb = new StringBuilder();
        sb.append("delete from " + table.getSchema().getName() + "." + table.getName() + " where ");
        StringBuilder sbConditions = new StringBuilder();
        int i = 1;
        String and = " AND ";
        for (Value v : values) {
            Column column = v.getColumn();
            if (column.isKey()) {
                indexes.put(column.getName(), i++);
                sbConditions.append(column.getName() + " = ?" + and);
            }
        }
        // if keys where not set
        if (i == 1) {
            for (Value v : values) {
                Column column = v.getColumn();
                indexes.put(column.getName(), i++);
                sbConditions.append(column.getName() + " = ?" + and);
            }
        }
        if (sbConditions.length() > and.length()) {
            sbConditions.setLength(sbConditions.length() - and.length());
        }
        sb.append(sbConditions);

        performIn(context, result, con, deleteWrapper(sb, expectedCount), table, indexes, values);
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
     * @param wrapper
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
    protected void performIn(IContext context, IResultSet result, Connection con, SqlWrapper wrapper, Table table, Map<String, Integer> indexes, Set<Value> values) throws PluginException, SQLException {
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(wrapper.getSql() + ". MAP: " + indexes + ". VALUES: " + values);
        }

        PreparedStatement pstmt = inputs.get(wrapper.getSql());
        if (pstmt == null) {
            pstmt = createStatement(con, table, wrapper);
            inputs.put(wrapper.getSql(), pstmt);
        } else {
            pstmt.clearParameters();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("REUSE: " + pstmt);
            }
        }

        idManager.clearLocal();
        for (Value v : values) {
            Column column = v.getColumn();
            Integer index = indexes.get(column.getName());
            if (index != null) {
                Object obj = v.getValue();
                if (column.isVirtual()) {
                    // the target table is the column header
                    obj = idManager.lookup(column.getAlias(), obj);
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("performIn.SET(" + index + "," + column.getAlias() + "," + column.getName() + ") = " + obj);
                }
                pstmt.setObject(index, obj);
                if (column.isReference()) {
                    idManager.addLocal(table.getAlias(), UtilEvaluator.replace(v.getCell().getValue(), context, true));
                }
            }
        }

        if (wrapper.getType() == CommandType.UPDATE) {
            idManager.prepareUpdate(con, table, values, outputs);
        }

        int count = pstmt.executeUpdate();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("[" + count + "]=" + wrapper.getSql());
        }

        idManager.readKeys(con, pstmt, wrapper, table, values);

        if (wrapper.getExpectedCount() != count) {
            throw new PluginException("The expected update count (" + wrapper.getExpectedCount() + ") does not match, received = " + count + ".\n\tSQL: " + wrapper.getSql() + "\n\tARGS: " + values);
        }
    }

    /**
     * Create the prepared statement.
     * 
     * @param con
     *            The connection.
     * @param table
     *            The table under analysis.
     * @param sqlWrapper
     *            The wrapper.
     * @return A new prepared statement.
     * @throws SQLException
     *             On creation errors.
     */
    protected PreparedStatement createStatement(Connection con, Table table, SqlWrapper sqlWrapper) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        if (meta.supportsGetGeneratedKeys()) {
            List<String> lista = new LinkedList<String>();
            for (Column c : table.getKeys()) {
                lista.add(c.getName());
            }
            return con.prepareStatement(sqlWrapper.getSql(), lista.toArray(new String[lista.size()]));
        } else {
            return con.prepareStatement(sqlWrapper.getSql());
        }
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
     * @param expectedCount
     *            The select expected count.
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performSelect(IContext context, IResultSet result, Connection con, Table table, Set<Value> values, int expectedCount) throws PluginException, SQLException {
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
                    value = idManager.findValue(con, column, value, outputs);
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("performOut.SET(" + index + "," + column.getAlias() + "," + column.getName() + ") = " + value);
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
                            value = idManager.findValue(con, column, value, outputs);
                        }
                        comparator.initialize();
                        if (!comparator.match(value, received)) {
                            Object expected = v.getValue();
                            if (column.isVirtual()) {
                                received = idManager.lookup(column.getAlias(), received);
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
        inputs.release();
        outputs.release();
    }
}