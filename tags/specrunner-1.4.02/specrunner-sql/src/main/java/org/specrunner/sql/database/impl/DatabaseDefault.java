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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.SRServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.database.CommandType;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.DatabaseRegisterEvent;
import org.specrunner.sql.database.DatabaseTableEvent;
import org.specrunner.sql.database.EMode;
import org.specrunner.sql.database.IColumnReader;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.IDatabaseListener;
import org.specrunner.sql.database.IIdManager;
import org.specrunner.sql.database.INullEmptyHandler;
import org.specrunner.sql.database.ISequenceProvider;
import org.specrunner.sql.database.ISqlWrapperFactory;
import org.specrunner.sql.database.IStatementFactory;
import org.specrunner.sql.database.SqlWrapper;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.ReplicableException;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.UtilNames;
import org.specrunner.sql.meta.Value;
import org.specrunner.sql.meta.impl.UtilSchema;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilSql;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.collections.ReverseIterable;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.IPresentation;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;
import org.specrunner.util.xom.core.PresentationCompare;
import org.specrunner.util.xom.core.PresentationException;

/**
 * Basic implementation of <code>IDatabase</code> using cached prepared
 * statements, an ID manager to work with generated keys, a sequence provider to
 * enable sequence interactions and a column reader to recover column objects.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class DatabaseDefault implements IDatabase {

    /**
     * A null/empty handler.
     */
    protected INullEmptyHandler nullEmptyHandler = new NullEmptyHandlerDefault();

    /**
     * Sequence next value generator.
     */
    protected ISequenceProvider sequenceProvider = new SequenceProviderDefault();

    /**
     * Recover object from a result set column to be compared against the
     * specification object.
     */
    protected IColumnReader columnReader = new ColumnReaderDefault();

    /**
     * Factory of SQLs.
     */
    protected ISqlWrapperFactory sqlWrapperFactory = new SqlWrapperFactoryDefault();

    /**
     * Factory of statements.
     */
    protected IStatementFactory statementFactory = new StatementFactoryDefault();

    /**
     * Manage object lookup and reuse.
     */
    protected IIdManager idManager = new IdManagerDefault();

    /**
     * List of listeners.
     */
    protected List<IDatabaseListener> listeners = new LinkedList<IDatabaseListener>();

    /**
     * Feature for database error dump limit.
     */
    public static final String FEATURE_LIMIT = DatabaseDefault.class.getName() + ".limit";

    /**
     * Feature for dump size.
     */
    private static final Integer DEFAULT_LIMIT = 100;

    /**
     * Max size of errors dump.
     */
    private Integer limit = DEFAULT_LIMIT;

    @Override
    public void initialize() {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_NULL_EMPTY_HANDLER, this);
        fm.set(FEATURE_SEQUENCE_PROVIDER, this);
        fm.set(FEATURE_COLUMN_READER, this);
        fm.set(FEATURE_SQL_WRAPPER_FACTORY, this);
        fm.set(FEATURE_STATEMENT_FACTORY, this);
        fm.set(FEATURE_ID_MANAGER, this);
        fm.set(FEATURE_LISTENERS, this);
        fm.set(FEATURE_LIMIT, this);
        // every use of database clear mappings to avoid memory overload and
        // test interference
        idManager.reset();
    }

    /**
     * Get the null/empty handler.
     * 
     * @return Current null/empty handler.
     */
    public INullEmptyHandler getNullEmptyHandler() {
        return nullEmptyHandler;
    }

    @Override
    public void setNullEmptyHandler(INullEmptyHandler nullEmptyHandler) {
        this.nullEmptyHandler = nullEmptyHandler;
    }

    /**
     * Get the sequence values provider.
     * 
     * @return The provider.
     */
    public ISequenceProvider getSequenceProvider() {
        return sequenceProvider;
    }

    @Override
    public void setSequenceProvider(ISequenceProvider sequenceProvider) {
        this.sequenceProvider = sequenceProvider;
    }

    /**
     * Get current column reader.
     * 
     * @return The current reader.
     */
    public IColumnReader getColumnReader() {
        return columnReader;
    }

    @Override
    public void setColumnReader(IColumnReader columnReader) {
        this.columnReader = columnReader;
    }

    /**
     * Get the SQL wrapper factory.
     * 
     * @return The current factory.
     */
    public ISqlWrapperFactory getSqlWrapperFactory() {
        return sqlWrapperFactory;
    }

    @Override
    public void setSqlWrapperFactory(ISqlWrapperFactory sqlWrapperFactory) {
        this.sqlWrapperFactory = sqlWrapperFactory;
    }

    /**
     * Get statement factory.
     * 
     * @return The current factory.
     */
    public IStatementFactory getStatementFactory() {
        return statementFactory;
    }

    @Override
    public void setStatementFactory(IStatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    /**
     * Get the id manager.
     * 
     * @return The manager.
     */
    public IIdManager getIdManager() {
        return idManager;
    }

    @Override
    public void setIdManager(IIdManager idManager) {
        this.idManager = idManager;
    }

    /**
     * Get listeners.
     * 
     * @return Listeners.
     */
    public List<IDatabaseListener> getListeners() {
        return listeners;
    }

    @Override
    public void setListeners(List<IDatabaseListener> listeners) {
        if (listeners == null) {
            throw new IllegalArgumentException("Listeners cannot be a null list.");
        }
        this.listeners = listeners;
    }

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
     * Fire initialize event.
     */
    protected void fireInitialize() {
        synchronized (listeners) {
            for (IDatabaseListener listener : listeners) {
                listener.initialize();
            }
        }
    }

    /**
     * Fire table in event.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On processing errors.
     */
    protected void fireTableIn(DatabaseTableEvent event) throws DatabaseException {
        synchronized (listeners) {
            for (IDatabaseListener listener : listeners) {
                listener.onTableIn(event);
            }
        }
    }

    /**
     * Fire register in event.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On processing errors.
     */
    protected void fireRegisterIn(DatabaseRegisterEvent event) throws DatabaseException {
        synchronized (listeners) {
            for (IDatabaseListener listener : listeners) {
                listener.onRegisterIn(event);
            }
        }
    }

    /**
     * Fire register out event.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On processing errors.
     */
    protected void fireRegisterOut(DatabaseRegisterEvent event) throws DatabaseException {
        synchronized (listeners) {
            for (IDatabaseListener listener : new ReverseIterable<IDatabaseListener>(listeners)) {
                listener.onRegisterOut(event);
            }
        }
    }

    /**
     * Fire table out event.
     * 
     * @param event
     *            Event.
     * @throws DatabaseException
     *             On processing errors.
     */
    protected void fireTableOut(DatabaseTableEvent event) throws DatabaseException {
        synchronized (listeners) {
            for (IDatabaseListener listener : new ReverseIterable<IDatabaseListener>(listeners)) {
                listener.onTableOut(event);
            }
        }
    }

    @Override
    public void perform(IContext context, IResultSet result, TableAdapter adapter, Connection connection, Schema schema, EMode mode) throws DatabaseException {
        List<CellAdapter> captions = adapter.getCaptions();
        if (captions.isEmpty()) {
            throw new DatabaseException("Tables must have a caption. The caption must be part of this set: " + schema.getAliasToTables().keySet());
        }
        String tAlias = captions.get(0).getValue();
        Table table = schema.getAlias(tAlias);
        if (table == null) {
            throw new DatabaseException("Table '" + UtilNames.normalize(tAlias) + "' not found in schema " + schema.getAlias() + "(" + schema.getName() + "), available tables: " + schema.getAliasToTables().keySet());
        }
        // creates a copy only of defined tables
        try {
            table = table.copy();
        } catch (ReplicableException e) {
            throw new DatabaseException("Cannot create a copy of table " + table.getName() + " with alias " + table.getAlias() + ".", e);
        }
        List<RowAdapter> rows = adapter.getRows();
        if (rows.isEmpty() || rows.size() == 1) {
            throw new DatabaseException("A valid table should have at least 2 rows, one for headers (th's) and another for values (td's).");
        }
        // headers are in the first row.
        RowAdapter header = rows.get(0);
        List<CellAdapter> headers = header.getCells();
        Column[] columns = new Column[headers.size()];
        readHeadersColumns(context, table, headers, columns);

        // clear listeners
        fireInitialize();

        // start using listeners
        fireTableIn(new DatabaseTableEvent(context, result, adapter, connection, table, mode));

        for (int i = 1; i < rows.size(); i++) {
            RowAdapter row = rows.get(i);
            List<CellAdapter> tds = row.getCells();
            if (tds.isEmpty()) {
                throw new DatabaseException("Empty lines are useless. Invalid row[" + i + "]:" + row.getValue());
            }
            if (tds.size() != headers.size()) {
                throw new DatabaseException("Invalid number of cells at row: " + i + ". Expected " + headers.size() + " columns, received " + tds.size() + ".\n\t ROW:" + row);
            }
            int expectedCount = Integer.parseInt(row.getAttribute("count", "1"));
            String type = tds.get(0).getValue();
            CommandType command = CommandType.get(type);
            if (command == null) {
                throw new DatabaseException("Invalid command type. '" + type + "' at (row: " + i + ", cell: 0). The first column is required for one of the following values: " + Arrays.toString(CommandType.values()));
            }
            Map<String, Value> filled = new HashMap<String, Value>();
            IRegister register = new RegisterDefault();
            for (int j = 1; j < tds.size(); j++) {
                Column column = columns[j].copy();
                CellAdapter td = tds.get(j);
                try {
                    UtilSchema.setupColumn(context, table, column, td);
                } catch (ConverterException e) {
                    throw new DatabaseException(e);
                } catch (ComparatorException e) {
                    throw new DatabaseException(e);
                }
                String content = getAdjustContent(context, td);
                try {
                    Value v = getValue(mode, command, column, td, content);
                    if (v != null) {
                        register.add(v);
                        filled.put(column.getName(), v);
                    }
                } catch (ConverterException e) {
                    result.addResult(Failure.INSTANCE, context.newBlock(td.getNode(), context.getPlugin()), new PluginException("Convertion error at row: " + i + ", cell: " + j + ".", e));
                    continue;
                }
            }
            try {
                boolean error = false;
                switch (command) {
                case INSERT:
                    if (mode == EMode.INPUT) {
                        performInsert(context, result, connection, table, register, filled);
                    } else {
                        performSelect(context, result, connection, table, command, register, expectedCount);
                    }
                    break;
                case UPDATE:
                    if (mode == EMode.INPUT) {
                        performUpdate(context, result, connection, table, register, expectedCount);
                    } else {
                        performSelect(context, result, connection, table, command, register, expectedCount);
                    }
                    break;
                case DELETE:
                    if (mode == EMode.INPUT) {
                        performDelete(context, result, connection, table, register, expectedCount);
                    } else {
                        performSelect(context, result, connection, table, command, register, 0);
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
                    result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), context.getPlugin()), new PluginException("Error in connection (" + connection.getMetaData().getURL() + "): " + e.getMessage(), e));
                } catch (SQLException e1) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new DatabaseException("Could not log error:" + e1.getMessage(), e1);
                }
            } catch (DatabaseException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), context.getPlugin()), e);
            }
        }

        fireTableOut(new DatabaseTableEvent(context, result, adapter, connection, table, mode));
    }

    /**
     * Read headers information.
     * 
     * @param context
     *            The test context.
     * @param table
     *            The current table.
     * @param headers
     *            The headers list.
     * @param columns
     *            The columns list.
     * @throws DatabaseException
     *             On reading errors.
     */
    protected void readHeadersColumns(IContext context, Table table, List<CellAdapter> headers, Column[] columns) throws DatabaseException {
        for (int i = 0; i < headers.size(); i++) {
            CellAdapter cell = headers.get(i);
            String cAlias = cell.getValue();
            columns[i] = table.getAlias(cAlias);
            Column column = columns[i];
            if (i > 0 && column == null) {
                throw new DatabaseException("Column with alias '" + cAlias + "' not found in:" + table.getAliasToColumns().keySet());
            }
            // update to specific header adjusts
            if (column != null) {
                try {
                    UtilSchema.setupColumn(context, table, column, cell);
                } catch (ConverterException e) {
                    throw new DatabaseException(e);
                } catch (ComparatorException e) {
                    throw new DatabaseException(e);
                }
            }
        }
    }

    /**
     * Get the string value of a node holder, and adjust text if required.
     * 
     * @param context
     *            The context.
     * @param nh
     *            A node holder.
     * @return The interpreted string value.
     * @throws DatabaseException
     *             On evaluation errors.
     */
    protected String getAdjustContent(IContext context, INodeHolder nh) throws DatabaseException {
        try {
            String previous = nh.getValue();
            String value = UtilEvaluator.replace(nh.getAttribute(INodeHolder.ATTRIBUTE_VALUE, previous), context, true);
            // if text has changed... adjust on screen.
            if (previous != null && !previous.equals(value)) {
                nh.setValue(value);
            }
            return value;
        } catch (PluginException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * Get value object for a given cell.
     * 
     * @param mode
     *            Action mode.
     * @param command
     *            Command type.
     * @param column
     *            Column meta-data.
     * @param td
     *            The cell.
     * @param content
     *            Cell content.
     * @return A value, if valid, null, otherwise.
     * @throws ConverterException
     *             On data conversion errors.
     */
    protected Value getValue(EMode mode, CommandType command, Column column, CellAdapter td, String content) throws ConverterException {
        boolean isNull = nullEmptyHandler.isNull(content, mode);
        boolean isEmpty = nullEmptyHandler.isEmpty(content, mode);
        boolean isVirtual = column.isVirtual();
        IConverter converter = column.getConverter();
        if (isNull || isEmpty || isVirtual || converter.accept(content)) {
            Object obj = null;
            if (isNull) {
                obj = null;
            } else if (isEmpty) {
                obj = "";
            } else if (isVirtual) {
                obj = content;
            } else {
                List<String> args = column.getArguments();
                obj = converter.convert(content, args.isEmpty() ? null : args.toArray());
            }
            if (obj == null && command == CommandType.INSERT && mode == EMode.INPUT) {
                // the other column fields with default value are set in
                // <code>addMissingValues(...)</code> method.
                obj = column.getDefaultValue();
            }
            return new Value(column, td, obj, column.getComparator());
        }
        return null;
    }

    /**
     * Perform database inserts.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param connection
     *            The connection.
     * @param table
     *            The specification.
     * @param register
     *            The values.
     * @param filled
     *            Filled fields.
     * @throws DatabaseException
     *             On database errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performInsert(IContext context, IResultSet result, Connection connection, Table table, IRegister register, Map<String, Value> filled) throws DatabaseException, SQLException {
        addMissingValues(table, register, filled);
        performIn(context, result, connection, sqlWrapperFactory.createInputWrapper(table, CommandType.INSERT, register, 1), table, register);
    }

    /**
     * Add missing values to insert value set.
     * 
     * @param table
     *            The table.
     * @param register
     *            The set of values.
     * @param filled
     *            A map of filled fields.
     */
    protected void addMissingValues(Table table, IRegister register, Map<String, Value> filled) {
        for (Column column : table.getColumns()) {
            // table columns not present in test table
            if (filled.get(column.getName()) == null) {
                if (column.getDefaultValue() != null) {
                    // with default values should be set
                    register.add(new Value(column, null, column.getDefaultValue(), column.getComparator()));
                } else if (column.isSequence()) {
                    // or if it is a sequence: add their next value command.
                    register.add(new Value(column, null, sequenceProvider.nextValue(column.getSequence()), column.getComparator()));
                }
            }
        }
    }

    /**
     * Perform database updates.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param connection
     *            The connection.
     * @param table
     *            The specification.
     * @param register
     *            The values.
     * @param expectedCount
     *            The expected action counter.
     * @throws DatabaseException
     *             On database errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performUpdate(IContext context, IResultSet result, Connection connection, Table table, IRegister register, int expectedCount) throws DatabaseException, SQLException {
        performIn(context, result, connection, sqlWrapperFactory.createInputWrapper(table, CommandType.UPDATE, register, expectedCount), table, register);
    }

    /**
     * Perform database deletes.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param connection
     *            The connection.
     * @param table
     *            The specification.
     * @param register
     *            The values.
     * @param expectedCount
     *            The delete expected count.
     * @throws DatabaseException
     *             On database errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performDelete(IContext context, IResultSet result, Connection connection, Table table, IRegister register, int expectedCount) throws DatabaseException, SQLException {
        performIn(context, result, connection, sqlWrapperFactory.createInputWrapper(table, CommandType.DELETE, register, expectedCount), table, register);
    }

    /**
     * Perform database commands.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param connection
     *            The connection.
     * @param wrapper
     *            The SQL wrapper.
     * @param table
     *            The target table.
     * @param register
     *            The values.
     * @throws DatabaseException
     *             On database errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performIn(IContext context, IResultSet result, Connection connection, SqlWrapper wrapper, Table table, IRegister register) throws DatabaseException, SQLException {
        Map<String, Integer> namesToIndexes = wrapper.getNamesToIndexes();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(wrapper.getSql() + ". MAP: " + namesToIndexes + ". VALUES: " + register);
        }
        idManager.clear();

        PreparedStatement pstmt = statementFactory.getInput(connection, wrapper.getSql(), table);
        Map<Integer, Object> indexesToValues = prepareInputValues(table, register, namesToIndexes);
        for (Entry<Integer, Object> e : indexesToValues.entrySet()) {
            pstmt.setObject(e.getKey(), e.getValue());
        }

        int count = pstmt.executeUpdate();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("[" + count + "]=" + wrapper.getSql());
        }
        if (wrapper.getExpectedCount() != count) {
            throw new DatabaseException("The expected count (" + wrapper.getExpectedCount() + ") does not match, received = " + count + ".\n\tSQL: " + wrapper.getSql() + "\n\tARGS: " + register);
        }

        DatabaseMetaData meta = connection.getMetaData();
        if (idManager.hasKeys() && meta.supportsGetGeneratedKeys()) {
            idManager.readKeys(pstmt);
        }

        fireRegisterIn(new DatabaseRegisterEvent(context, result, connection, table, register, wrapper, indexesToValues));
    }

    /**
     * Prepare values to use in insert/update/delete.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param namesToIndexes
     *            Map from indexes to values.
     * @return A map of object to use in statement setup.
     * @throws DatabaseException
     *             On prepare errors.
     */
    protected Map<Integer, Object> prepareInputValues(Table table, IRegister register, Map<String, Integer> namesToIndexes) throws DatabaseException {
        Map<Integer, Object> indexesToValues = new HashMap<Integer, Object>();
        for (Value v : register) {
            Column column = v.getColumn();
            Integer index = namesToIndexes.get(column.getName());
            if (index != null) {
                String tableOrAlias = register.getTableOrAlias(column);
                Object obj = v.getValue();
                if (column.isVirtual()) {
                    obj = idManager.lookup(tableOrAlias, String.valueOf(obj));
                }
                if (column.isReference()) {
                    idManager.append(table.getAlias(), v.getCell().getValue());
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("performIn.SET(" + index + "," + column.getName() + ") = " + obj);
                }
                indexesToValues.put(index, obj);
            }
        }
        return indexesToValues;
    }

    /**
     * Perform database select verifications.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param connection
     *            The connection.
     * @param table
     *            The specification.
     * @param command
     *            Command type.
     * @param register
     *            The values.
     * @param expectedCount
     *            The select expected count.
     * @throws DatabaseException
     *             On database errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performSelect(IContext context, IResultSet result, Connection connection, Table table, CommandType command, IRegister register, int expectedCount) throws DatabaseException, SQLException {
        performOut(context, result, connection, sqlWrapperFactory.createOutputWrapper(table, command, register, expectedCount), table, register);
    }

    /**
     * Perform database selects.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param connection
     *            The connection.
     * @param wrapper
     *            The SQL wrapper.
     * @param table
     *            The output table.
     * @param register
     *            The values.
     * @throws DatabaseException
     *             On database errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performOut(IContext context, IResultSet result, Connection connection, SqlWrapper wrapper, Table table, IRegister register) throws DatabaseException, SQLException {
        Map<String, Integer> namesToIndexes = wrapper.getNamesToIndexes();
        String sql = wrapper.getSql();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + namesToIndexes + ". values = " + register + ". indexes = " + namesToIndexes);
        }
        PreparedStatement pstmt = statementFactory.getOutput(connection, wrapper.getSql(), table);
        Map<Integer, Object> indexesToValues = prepareSelectValues(connection, register, namesToIndexes);
        for (Entry<Integer, Object> e : indexesToValues.entrySet()) {
            pstmt.setObject(e.getKey(), e.getValue());
        }
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            if (wrapper.getExpectedCount() == 1) {
                if (!rs.next()) {
                    throw new DatabaseException("None register found with the given conditions: " + sql + " and values: [" + register + "]");
                }
                compareRegister(context, result, connection, register, namesToIndexes, rs);
                if (rs.next()) {
                    throw new DatabaseException("More than one register satisfy the condition: " + sql + "[" + register + "]\n" + dumpRs("Extra itens:", rs));
                }
            } else {
                if (rs.next()) {
                    throw new DatabaseException("A result for " + sql + "[" + register + "] was not expected.\n" + dumpRs("Unexpected items:", rs));
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        fireRegisterOut(new DatabaseRegisterEvent(context, result, connection, table, register, wrapper, indexesToValues));
    }

    /**
     * Prepare values to use in select.
     * 
     * @param connection
     *            A connection.
     * @param register
     *            A register.
     * @param namesToIndexes
     *            Map from indexes to values.
     * @return A map of object to use in statement setup.
     * @throws DatabaseException
     *             On prepare errors.
     * @throws SQLException
     *             On database errors.
     */
    protected Map<Integer, Object> prepareSelectValues(Connection connection, IRegister register, Map<String, Integer> namesToIndexes) throws DatabaseException, SQLException {
        Map<Integer, Object> indexesToValues = new HashMap<Integer, Object>();
        for (Value v : register) {
            Column column = v.getColumn();
            Integer index = namesToIndexes.get(column.getName());
            if (index != null) {
                Object value = v.getValue();
                if (column.isVirtual()) {
                    value = idManager.find(register.getTableOrAlias(column), String.valueOf(value), column, connection, statementFactory);
                }
                if (column.isDate()) {
                    IComparator comp = column.getComparator();
                    if (!(comp instanceof ComparatorDate)) {
                        throw new DatabaseException("Date columns must have comparators of type 'date'. Current type:" + comp.getClass());
                    }
                    ComparatorDate comparator = (ComparatorDate) comp;
                    comparator.initialize();
                    Date dateBefore = new Date(((Date) value).getTime() - comparator.getTolerance());
                    Date dateAfter = new Date(((Date) value).getTime() + comparator.getTolerance());
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("performOut.SET(" + (index) + "," + column.getAlias() + "," + column.getName() + ") = " + dateBefore);
                        UtilLog.LOG.debug("performOut.SET(" + (index + 1) + "," + column.getAlias() + "," + column.getName() + ") = " + dateAfter);
                    }
                    indexesToValues.put(index, dateBefore);
                    indexesToValues.put(index + 1, dateAfter);
                } else {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("performOut.SET(" + index + "," + column.getAlias() + "," + column.getName() + ") = " + value);
                    }
                    indexesToValues.put(index, value);
                }
                v.setValue(value);
            }
        }
        return indexesToValues;
    }

    /**
     * Compare register with current result set item.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * @param connection
     *            The connection.
     * @param register
     *            The register.
     * @param namesToIndexes
     *            A mapping from column names to indexes.
     * @param rs
     *            A result.
     * @throws DatabaseException
     *             On compare errors.
     * @throws SQLException
     *             On database errors.
     */
    protected void compareRegister(IContext context, IResultSet result, Connection connection, IRegister register, Map<String, Integer> namesToIndexes, ResultSet rs) throws DatabaseException, SQLException {
        for (Value v : register) {
            Column column = v.getColumn();
            Integer index = namesToIndexes.get(column.getName());
            if (index == null) {
                IComparator comparator = v.getComparator();
                Object received = columnReader.read(rs, column);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("CHECK(" + v.getValue() + ") = " + received);
                }
                Object value = v.getValue();
                String tableOrAlias = register.getTableOrAlias(column);
                if (column.isVirtual()) {
                    value = idManager.find(register.getTableOrAlias(column), String.valueOf(value), column, connection, statementFactory);
                }
                comparator.initialize();
                CellAdapter cell = v.getCell();
                if (!comparator.match(value, received)) {
                    Object expected = v.getValue();
                    if (column.isVirtual()) {
                        received = idManager.lookup(tableOrAlias, String.valueOf(received));
                    }
                    String expStr = UtilSql.toString(expected);
                    String recStr = UtilSql.toString(received);
                    shortView(cell, expected, expStr, recStr);
                    IPresentation error = null;
                    if (expStr.equals(recStr)) {
                        error = new PresentationCompare(expected, received);
                    } else {
                        error = new DefaultAlignmentException("Values are different.", expStr, recStr);
                    }
                    result.addResult(Failure.INSTANCE, context.newBlock(cell.getNode(), context.getPlugin()), new PresentationException(error));
                } else {
                    String str = String.valueOf(value);
                    if (!str.equals(cell.getValue())) {
                        cell.append(" {" + str + "}");
                    }
                }
            }
        }
    }

    /**
     * Create an error information to append to a cell.
     * 
     * @param cell
     *            A cell.
     * @param expected
     *            Expected object.
     * @param expStr
     *            Expected as string.
     * @param recStr
     *            Received as string.
     */
    protected void shortView(CellAdapter cell, Object expected, String expStr, String recStr) {
        if (!expStr.equals(expected)) {
            Element spanExp = new Element("span");
            spanExp.addAttribute(new Attribute("class", "compare"));
            spanExp.appendChild(new Element("br"));
            {
                Element spanLabelExp = new Element("span");
                spanLabelExp.addAttribute(new Attribute("class", "expected"));
                spanExp.appendChild(spanLabelExp);

                spanLabelExp.appendChild(" (expected):");
            }
            spanExp.appendChild(expStr);
            cell.append(spanExp);
        }
        Element spanRec = new Element("span");
        spanRec.addAttribute(new Attribute("class", "compare"));
        spanRec.appendChild(new Element("br"));
        {
            Element spanLabelRec = new Element("span");
            spanLabelRec.addAttribute(new Attribute("class", "received"));
            spanRec.appendChild(spanLabelRec);

            spanLabelRec.appendChild(" (received):");
        }
        spanRec.appendChild(recStr);
        cell.append(spanRec);
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
        statementFactory.release();
    }
}