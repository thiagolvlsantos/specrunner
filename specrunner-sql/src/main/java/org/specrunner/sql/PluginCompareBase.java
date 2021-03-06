/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.specrunner.SRServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.expressions.EMode;
import org.specrunner.expressions.INullEmptyFeature;
import org.specrunner.expressions.INullEmptyHandler;
import org.specrunner.expressions.core.NullEmptyHandlerDefault;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.database.IColumnReader;
import org.specrunner.sql.database.IDatabaseReader;
import org.specrunner.sql.database.impl.ColumnReaderDefault;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IDataFilter;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.impl.DataFilterDefault;
import org.specrunner.sql.report.LineReport;
import org.specrunner.sql.report.RegisterType;
import org.specrunner.sql.report.ReportException;
import org.specrunner.sql.report.SchemaReport;
import org.specrunner.sql.report.TableReport;
import org.specrunner.util.UtilLog;

/**
 * Provided a <code>Schema</code> instance, and two databases, this plugin
 * compare these databases. The database used by the system under test name is '
 * <code>system</code>' and the reference database is '<code>reference</code>'.
 * If some tables are expected to be ignored in comparison provide an instance
 * of <code>IFilter</code> using <code>PluginFilter</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareBase extends AbstractPluginValue implements INullEmptyFeature, IDatabaseReader {

    /**
     * Feature for schema name.
     */
    public static final String FEATURE_SCHEMA = PluginCompareBase.class.getName() + ".schema";
    /**
     * The schema name.
     */
    private String schema;
    /**
     * Feature for system datasource name.
     */
    public static final String FEATURE_SYSTEM = PluginCompareBase.class.getName() + ".system";
    /**
     * The system datasource.
     */
    private String system;
    /**
     * Feature for reference datasource name.
     */
    public static final String FEATURE_REFERENCE = PluginCompareBase.class.getName() + ".reference";
    /**
     * The guide datasource.
     */
    private String reference;

    /**
     * Feature for comparison filter.
     */
    public static final String FEATURE_FILTER = PluginCompareBase.class.getName() + ".filter";
    /**
     * Pattern name to be used.
     */
    private String filter;

    /**
     * A null/empty handler.
     */
    protected INullEmptyHandler nullEmptyHandler = new NullEmptyHandlerDefault();
    /**
     * Recover object from a result set column to be compared against the
     * specification object.
     */
    protected IColumnReader columnReader = new ColumnReaderDefault();

    /**
     * Feature for virtual keys usage.
     */
    public static final String FEATURE_VIRTUAL = PluginCompareBase.class.getName() + ".virtual";
    /**
     * Enable use of virtual keys for comparison table select. If enabled,
     * tables with 'reference' field will be compared by these fields, the
     * others will remain using 'key' fields;
     */
    private Boolean virtual;

    /**
     * Get the schema name.
     * 
     * @return The schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the schema name.
     * 
     * @param schema
     *            The name.
     */
    @DontEval
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Get the system database name.
     * 
     * @return The system database name.
     */
    public String getSystem() {
        return system;
    }

    /**
     * Set the system database name.
     * 
     * @param system
     *            The database name.
     */
    @DontEval
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * Get the reference database name.
     * 
     * @return The reference name.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Set the reference database name.
     * 
     * @param reference
     *            The reference.
     */
    @DontEval
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Get the filter name.
     * 
     * @return The name.
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Set the filter name.
     * 
     * @param filter
     *            Name.
     */
    @DontEval
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Get handler.
     * 
     * @return A null/empty handler.
     */
    public INullEmptyHandler getNullEmptyHandler() {
        return nullEmptyHandler;
    }

    @Override
    public void setNullEmptyHandler(INullEmptyHandler nullEmptyHandler) {
        this.nullEmptyHandler = nullEmptyHandler;
    }

    /**
     * Column reader resource.
     * 
     * @return A reader.
     */
    public IColumnReader getColumnReader() {
        return columnReader;
    }

    @Override
    public void setColumnReader(IColumnReader columnReader) {
        this.columnReader = columnReader;
    }

    /**
     * Indicate if reference fields can be used for select construction.
     * 
     * @return true, if the answer is yes, false, otherwise.
     */
    public Boolean getVirtual() {
        return virtual;
    }

    /**
     * Set the virtual flag.
     * 
     * @param virtual
     *            The flag.
     */
    public void setVirtual(Boolean virtual) {
        this.virtual = virtual;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_SCHEMA, this);
        fm.set(FEATURE_SYSTEM, this);
        fm.set(FEATURE_REFERENCE, this);
        fm.set(INullEmptyFeature.FEATURE_NULL_EMPTY_HANDLER, this);
        fm.set(IDatabaseReader.FEATURE_COLUMN_READER, this);
        fm.set(FEATURE_FILTER, this);
        fm.set(FEATURE_VIRTUAL, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Schema schema = PluginSchema.getSchema(context, getSchema());
        IDataFilter currentFilter = null;
        if (getFilter() != null) {
            currentFilter = PluginFilter.getFilter(context, getFilter());
        } else {
            currentFilter = new DataFilterDefault();
        }
        currentFilter.setup(context, EMode.COMPARE, schema);
        if (!currentFilter.accept(EMode.COMPARE, schema)) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Schema ignored:" + schema.getAlias() + "(" + schema.getName() + ")");
            }
            result.addResult(Success.INSTANCE, context.peek());
            return ENext.DEEP;
        }
        IDataSourceProvider expected = PluginConnection.getProvider(context, getReference());
        IDataSourceProvider received = PluginConnection.getProvider(context, getSystem());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("     Schema provider:" + schema);
            UtilLog.LOG.debug("   Datasource system:" + received);
            UtilLog.LOG.debug("Datasource reference:" + expected);
        }
        DataSource dsExcepted = expected.getDataSource();
        DataSource dsReceived = received.getDataSource();
        Connection connectionExpected = null;
        Statement stmtExpected = null;
        Connection connectionReceived = null;
        Statement stmtReceived = null;
        SchemaReport report = new SchemaReport(schema);
        try {
            connectionExpected = dsExcepted.getConnection();
            stmtExpected = connectionExpected.createStatement();
            connectionReceived = dsReceived.getConnection();
            stmtReceived = connectionReceived.createStatement();
            if (UtilLog.LOG.isDebugEnabled()) {
                String simpleName = getClass().getSimpleName();
                UtilLog.LOG.debug(simpleName + " connection expected:" + connectionExpected);
                UtilLog.LOG.debug(simpleName + " connection received:" + connectionReceived);
                UtilLog.LOG.debug(simpleName + "  statement expected:" + stmtExpected);
                UtilLog.LOG.debug(simpleName + "  statement received:" + stmtReceived);
            }
            for (Table table : schema.getTables()) {
                if (!currentFilter.accept(EMode.COMPARE, table)) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Table ignored:" + table.getAlias() + "(" + table.getName() + ")");
                    }
                    continue;
                }
                String sql = createTableSelect(schema, table);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Compare table (" + table.getName() + ", " + table.getAlias() + ") = " + sql);
                }
                ResultSet rsExpected = null;
                ResultSet rsReceived = null;
                try {
                    rsExpected = stmtExpected.executeQuery(sql);
                    rsReceived = stmtReceived.executeQuery(sql);
                    populateTableReport(schema, currentFilter, report, table, rsExpected, rsReceived);
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new PluginException(e);
                } finally {
                    try {
                        if (rsExpected != null) {
                            rsExpected.close();
                        }
                    } catch (Exception e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        throw new PluginException(e);
                    } finally {
                        try {
                            if (rsReceived != null) {
                                rsReceived.close();
                            }
                        } catch (Exception e) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e.getMessage(), e);
                            }
                            throw new PluginException(e);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        } finally {
            try {
                if (stmtExpected != null) {
                    stmtExpected.close();
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            } finally {
                try {
                    if (stmtReceived != null) {
                        stmtReceived.close();
                    }
                } catch (SQLException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new PluginException(e);
                }
            }
        }
        if (!report.isEmpty()) {
            result.addResult(Failure.INSTANCE, context.peek(), new ReportException(report));
        } else {
            result.addResult(Success.INSTANCE, context.peek());
        }
        return ENext.DEEP;
    }

    /**
     * Creates the SQL to be used by comparison algorithm.
     * 
     * @param schema
     *            The schema.
     * @param table
     *            The table.
     * @return The corresponding SQL.
     */
    public String createTableSelect(Schema schema, Table table) {
        StringBuilder fields = new StringBuilder();
        StringBuilder keys = new StringBuilder();
        StringBuilder references = new StringBuilder();
        int indexFields = 0;
        int indexKeys = 0;
        int indexReferences = 0;
        for (Column c : table.getColumns()) {
            fields.append((indexFields++ > 0 ? "," : "") + c.getName());
            if (c.isKey()) {
                keys.append((indexKeys++ > 0 ? "," : "") + c.getName() + " asc");
            }
            if (c.isReference()) {
                references.append((indexReferences++ > 0 ? "," : "") + c.getName() + " asc");
            }
        }
        StringBuilder order = keys;
        if (virtual != null && virtual && references.length() > 0) {
            order = references;
        }
        return "select " + fields + " from " + schema.getName() + "." + table.getName() + (order.length() > 0 ? " order by " + order : "");
    }

    /**
     * Populate schema report.
     * 
     * @param schema
     *            The schema object.
     * @param dataFilter
     *            The filter object.
     * @param report
     *            The report.
     * @param table
     *            The current table under analysis.
     * @param rsExpected
     *            The result set of reference database.
     * @param rsReceived
     *            The result set of system database.
     * @throws SQLException
     *             On comparison errors.
     */
    public void populateTableReport(Schema schema, IDataFilter dataFilter, SchemaReport report, Table table, ResultSet rsExpected, ResultSet rsReceived) throws SQLException {
        IResultEnumerator comp = getEnumerator(table, rsExpected, rsReceived);
        TableReport tr = new TableReport(table);
        while (comp.next()) {
            ResultSet exp = comp.getExpected();
            ResultSet rec = comp.getReceived();
            LineReport lr = null;
            int index = 0;
            if (exp == null && rec != null) {
                lr = new LineReport(RegisterType.EXTRA, tr, nullEmptyHandler);
                for (Column c : table.getColumns()) {
                    if (!dataFilter.accept(EMode.COMPARE, c)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Column ignored:" + c.getAlias() + "(" + c.getName() + ")");
                        }
                        continue;
                    }
                    Object read = columnReader.read(rec, c);
                    if (read != null) {
                        lr.add(c, index++, null, read);
                    }
                }
                if (!lr.isEmpty()) {
                    tr.add(lr);
                }
            } else if (exp != null && rec == null) {
                lr = new LineReport(RegisterType.MISSING, tr, nullEmptyHandler);
                for (Column c : table.getColumns()) {
                    if (!dataFilter.accept(EMode.COMPARE, c)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Column ignored:" + c.getAlias() + "(" + c.getName() + ")");
                        }
                        continue;
                    }
                    Object read = columnReader.read(exp, c);
                    if (read != null) {
                        lr.add(c, index++, read, null);
                    }
                }
                if (!lr.isEmpty()) {
                    tr.add(lr);
                }
            } else {
                lr = new LineReport(RegisterType.DIFFERENT, tr, nullEmptyHandler);
                for (Column c : table.getColumns()) {
                    if (!dataFilter.accept(EMode.COMPARE, c)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Column ignored:" + c.getAlias() + "(" + c.getName() + ")");
                        }
                        continue;
                    }
                    Object objExp = columnReader.read(exp, c);
                    Object objRec = columnReader.read(rec, c);
                    if (!dataFilter.accept(EMode.COMPARE, c, objRec)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Value ignored(" + c.getAlias() + "," + c.getName() + "):" + objRec);
                        }
                        continue;
                    }
                    IComparator comparator = c.getComparator();
                    comparator.initialize();
                    try {
                        boolean match = comparator.match(objExp, objRec);
                        if (!c.isKey() && !match) {
                            lr.add(c, index++, objExp, objRec);
                        }
                    } catch (ComparatorException e) {
                        throw new SQLException("Error on comparison of values.", e);
                    }
                }
                if (!lr.isEmpty()) {
                    for (Column c : table.getKeys()) {
                        lr.add(c, index++, columnReader.read(exp, c), columnReader.read(rec, c));
                    }
                    tr.add(lr);
                }
            }
        }
        if (!tr.isEmpty()) {
            report.add(tr);
        }
    }

    /**
     * Abstraction of enumeration.
     * 
     * @param table
     *            The table source of result sets.
     * @param rsExpected
     *            The result from querying reference database.
     * @param rsReceived
     *            The result from querying system database.
     * @return A enumerator.
     */
    public IResultEnumerator getEnumerator(Table table, ResultSet rsExpected, ResultSet rsReceived) {
        return new ResultSetEnumerator(table, virtual, rsExpected, rsReceived);
    }
}
