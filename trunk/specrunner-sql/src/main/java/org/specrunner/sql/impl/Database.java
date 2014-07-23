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
package org.specrunner.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import org.specrunner.sql.CommandType;
import org.specrunner.sql.DatabaseRegisterEvent;
import org.specrunner.sql.DatabaseTableEvent;
import org.specrunner.sql.EMode;
import org.specrunner.sql.IRegister;
import org.specrunner.sql.SqlWrapper;
import org.specrunner.sql.meta.Column;
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
public class Database extends AbstractDatabase {

    /**
     * Feature for database error dump limit.
     */
    public static final String FEATURE_LIMIT = Database.class.getName() + ".limit";

    /**
     * Feature for object manager instance.
     */
    public static final String FEATURE_ID_MANAGER = Database.class.getName() + ".idManager";

    /**
     * Manage object lookup and reuse.
     */
    protected IdManager idManager = new IdManager();

    /**
     * Feature for dump size.
     */
    private static final Integer DEFAULT_LIMIT = 100;

    /**
     * Max size of errors dump.
     */
    private Integer limit = DEFAULT_LIMIT;

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
        super.initialize();
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_LIMIT, this);
        fm.set(FEATURE_ID_MANAGER, this);
        // every use of database clear mappings to avoid memory overload and
        // test interference
        idManager.clear();
        // clear listeners
        fireInitialize();
    }

    @Override
    public void perform(IContext context, IResultSet result, TableAdapter adapter, Connection connection, Schema schema, EMode mode) throws PluginException {
        List<CellAdapter> captions = adapter.getCaptions();
        if (captions.isEmpty()) {
            throw new PluginException("Tables must have a caption.");
        }
        String tAlias = captions.get(0).getValue();
        Table table = schema.getAlias(tAlias);
        if (table == null) {
            throw new PluginException("Table '" + UtilNames.normalize(tAlias) + "' not found in schema " + schema.getAlias() + "(" + schema.getName() + "), avaliable tables alias: " + schema.getAliasToTables().keySet());
        }
        // creates a copy only of defined tables
        try {
            table = table.copy();
        } catch (ReplicableException e) {
            throw new PluginException("Cannot create a copy of table " + table.getName() + " with alias " + table.getAlias() + ".", e);
        }
        List<RowAdapter> rows = adapter.getRows();

        // headers are in the first row.
        RowAdapter header = rows.get(0);
        List<CellAdapter> headers = header.getCells();
        Column[] columns = new Column[headers.size()];
        readHeadersColumns(context, table, headers, columns);

        fireTableIn(new DatabaseTableEvent(context, result, adapter, connection, table, mode));

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
            CommandType command = CommandType.get(type);
            if (command == null) {
                throw new PluginException("Invalid command type. '" + type + "' at (row: " + i + ", cell: 0). The first column is required for one of the following values: " + Arrays.toString(CommandType.values()));
            }
            Map<String, Value> filled = new HashMap<String, Value>();
            IRegister register = new RegisterDefault();
            for (int j = 1; j < tds.size(); j++) {
                Column column = columns[j].copy();
                CellAdapter td = tds.get(j);
                try {
                    UtilSchema.setupColumn(context, table, column, td);
                } catch (ConverterException e) {
                    throw new PluginException(e);
                } catch (ComparatorException e) {
                    throw new PluginException(e);
                }
                String value = getAdjustValue(context, td);
                boolean isNull = nullEmptyHandler.isNull(value, mode);
                boolean isEmpty = nullEmptyHandler.isEmpty(value, mode);
                IConverter converter = column.getConverter();
                if (isNull || isEmpty || column.isVirtual() || converter.accept(value)) {
                    Object obj = null;
                    if (isNull) {
                        obj = null;
                    } else if (isEmpty) {
                        obj = "";
                    } else if (column.isVirtual()) {
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
                    if (obj == null && command == CommandType.INSERT && mode == EMode.INPUT) {
                        // the other column fields with default value are set in
                        // <code>addMissingValues(...)</code> method.
                        obj = column.getDefaultValue();
                    }
                    Value v = new Value(column, td, obj, column.getComparator());
                    register.add(v);
                    filled.put(column.getName(), v);
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
            connection.commit();
        } catch (SQLException e) {
            throw new PluginException(e);
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
     * @throws PluginException
     *             On reading errors.
     */
    protected void readHeadersColumns(IContext context, Table table, List<CellAdapter> headers, Column[] columns) throws PluginException {
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
                    UtilSchema.setupColumn(context, table, column, cell);
                } catch (ConverterException e) {
                    throw new PluginException(e);
                } catch (ComparatorException e) {
                    throw new PluginException(e);
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
     * @throws PluginException
     *             On evaluation errors.
     */
    protected String getAdjustValue(IContext context, INodeHolder nh) throws PluginException {
        String previous = nh.getValue();
        String value = UtilEvaluator.replace(nh.getAttribute(INodeHolder.ATTRIBUTE_VALUE, previous), context, true);
        // if text has changed... adjust on screen.
        if (previous != null && !previous.equals(value)) {
            nh.setValue(value);
        }
        return value;
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
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performInsert(IContext context, IResultSet result, Connection connection, Table table, IRegister register, Map<String, Value> filled) throws PluginException, SQLException {
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
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performUpdate(IContext context, IResultSet result, Connection connection, Table table, IRegister register, int expectedCount) throws PluginException, SQLException {
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
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performDelete(IContext context, IResultSet result, Connection connection, Table table, IRegister register, int expectedCount) throws PluginException, SQLException {
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
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performIn(IContext context, IResultSet result, Connection connection, SqlWrapper wrapper, Table table, IRegister register) throws PluginException, SQLException {
        Map<String, Integer> namesToIndexes = wrapper.getNamesToIndexes();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(wrapper.getSql() + ". MAP: " + namesToIndexes + ". VALUES: " + register);
        }
        PreparedStatement pstmt = statementFactory.getInput(connection, wrapper, table);
        Map<Integer, Object> indexesToValues = prepareInputValues(table, register, namesToIndexes);
        for (Entry<Integer, Object> e : indexesToValues.entrySet()) {
            pstmt.setObject(e.getKey(), e.getValue());
        }

        if (wrapper.getType() == CommandType.UPDATE) {
            idManager.prepareUpdate(connection, table, register, statementFactory);
        }

        int count = pstmt.executeUpdate();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("[" + count + "]=" + wrapper.getSql());
        }

        idManager.readKeys(connection, pstmt, wrapper, table, register);

        if (wrapper.getExpectedCount() != count) {
            throw new PluginException("The expected count (" + wrapper.getExpectedCount() + ") does not match, received = " + count + ".\n\tSQL: " + wrapper.getSql() + "\n\tARGS: " + register);
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
     * @throws PluginException
     *             On prepare errors.
     */
    protected Map<Integer, Object> prepareInputValues(Table table, IRegister register, Map<String, Integer> namesToIndexes) throws PluginException {
        idManager.clearLocal();
        Map<Integer, Object> indexesToValues = new HashMap<Integer, Object>();
        for (Value v : register) {
            Column column = v.getColumn();
            Integer index = namesToIndexes.get(column.getName());
            if (index != null) {
                Object obj = v.getValue();
                String alias = column.getTableOrAlias();
                if (column.isVirtual()) {
                    // the target table is the column header
                    String pointer = column.getPointer();
                    if (pointer != null) {
                        alias = null;
                        for (Value vp : register) {
                            if (pointer.equals(vp.getColumn().getAlias())) {
                                alias = UtilNames.normalize(vp.getCell().getValue());
                                break;
                            }
                        }
                        if (alias == null) {
                            throw new PluginException("The column '" + column.getAlias() + "' point to a non-existing column '" + pointer + "' of this table. Adjust attribute 'pointer' into database mapping file.");
                        }
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("pointer(" + pointer + ") -> " + alias);
                        }
                    }
                    obj = idManager.lookup(alias, obj);
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("performIn.SET(" + index + "," + alias + "," + column.getName() + ") = " + obj);
                }
                indexesToValues.put(index, obj);
                if (column.isReference()) {
                    idManager.addLocal(table.getAlias(), v.getCell().getValue());
                }
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
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performSelect(IContext context, IResultSet result, Connection connection, Table table, CommandType command, IRegister register, int expectedCount) throws PluginException, SQLException {
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
     * @throws PluginException
     *             On plugin errors.
     * @throws SQLException
     *             On SQL errors.
     */
    protected void performOut(IContext context, IResultSet result, Connection connection, SqlWrapper wrapper, Table table, IRegister register) throws PluginException, SQLException {
        Map<String, Integer> namesToIndexes = wrapper.getNamesToIndexes();
        String sql = wrapper.getSql();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(sql + ". MAP:" + namesToIndexes + ". values = " + register + ". indexes = " + namesToIndexes);
        }
        PreparedStatement pstmt = statementFactory.getOutput(connection, wrapper, table);
        Map<Integer, Object> indexesToValues = prepareSelectValues(connection, register, namesToIndexes);
        for (Entry<Integer, Object> e : indexesToValues.entrySet()) {
            pstmt.setObject(e.getKey(), e.getValue());
        }
        ResultSet rs = null;
        try {
            rs = pstmt.executeQuery();
            if (wrapper.getExpectedCount() == 1) {
                if (!rs.next()) {
                    throw new PluginException("None register found with the given conditions: " + sql + " and values: [" + register + "]");
                }
                compareRegister(context, result, connection, register, namesToIndexes, rs);
                if (rs.next()) {
                    throw new PluginException("More than one register satisfy the condition: " + sql + "[" + register + "]\n" + dumpRs("Extra itens:", rs));
                }
            } else {
                if (rs.next()) {
                    throw new PluginException("A result for " + sql + "[" + register + "] was not expected.\n" + dumpRs("Unexpected items:", rs));
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
     * @throws PluginException
     *             On prepare errors.
     * @throws SQLException
     *             On database errors.
     */
    protected Map<Integer, Object> prepareSelectValues(Connection connection, IRegister register, Map<String, Integer> namesToIndexes) throws PluginException, SQLException {
        Map<Integer, Object> indexesToValues = new HashMap<Integer, Object>();
        for (Value v : register) {
            Column column = v.getColumn();
            Integer index = namesToIndexes.get(column.getName());
            if (index != null) {
                Object value = v.getValue();
                if (column.isVirtual()) {
                    Column virtual = column;
                    String pointer = column.getPointer();
                    if (pointer != null) {
                        virtual = column.copy();
                        String alias = null;
                        for (Value vp : register) {
                            if (pointer.equals(vp.getColumn().getAlias())) {
                                alias = String.valueOf(vp.getCell().getValue());
                                break;
                            }
                        }
                        if (alias == null) {
                            throw new PluginException("The column '" + column.getAlias() + "' point to a non-existing column '" + pointer + "' of this table. Adjust attribute 'pointer' to this column into database mapping file.");
                        } else {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug("pointer(" + pointer + ") -> " + alias);
                            }
                        }
                        virtual.setAlias(alias);
                    }
                    value = idManager.findValue(connection, virtual, value, statementFactory);
                }
                if (column.isDate()) {
                    IComparator comp = column.getComparator();
                    if (!(comp instanceof ComparatorDate)) {
                        throw new PluginException("Date columns must have comparators of type 'date'. Current type:" + comp.getClass());
                    }
                    ComparatorDate comparator = (ComparatorDate) comp;
                    comparator.initialize();
                    Date dateBefore = new Date(((Date) value).getTime() - comparator.getTolerance());
                    Date dateAfter = new Date(((Date) value).getTime() + comparator.getTolerance());
                    indexesToValues.put(index, dateBefore);
                    indexesToValues.put(index + 1, dateAfter);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("performOut.SET(" + (index) + "," + column.getAlias() + "," + column.getName() + ") = " + dateBefore);
                        UtilLog.LOG.debug("performOut.SET(" + (index + 1) + "," + column.getAlias() + "," + column.getName() + ") = " + dateAfter);
                    }
                } else {
                    indexesToValues.put(index, value);
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("performOut.SET(" + index + "," + column.getAlias() + "," + column.getName() + ") = " + value);
                    }
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
     * @throws PluginException
     *             On compare errors.
     * @throws SQLException
     *             On database errors.
     */
    protected void compareRegister(IContext context, IResultSet result, Connection connection, IRegister register, Map<String, Integer> namesToIndexes, ResultSet rs) throws PluginException, SQLException {
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
                Column virtual = null;
                if (column.isVirtual()) {
                    virtual = column;
                    String pointer = column.getPointer();
                    if (pointer != null) {
                        virtual = column.copy();
                        String alias = null;
                        for (Value vp : register) {
                            if (pointer.equalsIgnoreCase(vp.getColumn().getAlias())) {
                                alias = String.valueOf(vp.getCell().getValue());
                                break;
                            }
                        }
                        if (alias == null) {
                            throw new PluginException("The column '" + column.getAlias() + "' point to a non-existing column '" + pointer + "' of this table. Adjust attribute 'pointer' into database mapping file.");
                        } else {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug("pointer(" + pointer + ") -> " + alias);
                            }
                        }
                        virtual.setAlias(alias);
                    }
                    value = idManager.findValue(connection, virtual, value, statementFactory);
                }
                comparator.initialize();
                CellAdapter cell = v.getCell();
                if (!comparator.match(value, received)) {
                    Object expected = v.getValue();
                    if (column.isVirtual()) {
                        received = idManager.lookup(column.getAlias(), received);
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