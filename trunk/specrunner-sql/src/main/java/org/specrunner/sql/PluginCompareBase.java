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
package org.specrunner.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.report.FilterDefault;
import org.specrunner.sql.report.IFilter;
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
public class PluginCompareBase extends AbstractPluginValue {

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

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SpecRunnerServices.getFeatureManager();
        fm.set(FEATURE_SCHEMA, this);
        fm.set(FEATURE_SYSTEM, this);
        fm.set(FEATURE_REFERENCE, this);
        fm.set(FEATURE_FILTER, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Schema schema = PluginSchema.getSchema(context, getSchema());
        IFilter currentFilter = null;
        if (filter != null) {
            currentFilter = PluginFilter.getFilter(context, getFilter());
        } else {
            currentFilter = new FilterDefault();
        }
        currentFilter.setup(schema, context);
        if (!currentFilter.accept(schema)) {
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
                if (!currentFilter.accept(table)) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Table ignored:" + table.getAlias() + "(" + table.getName() + ")");
                    }
                    continue;
                }
                String sql = createTableSelect(schema, table);
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
        StringBuilder order = new StringBuilder();
        int indexFields = 0;
        int indexOrder = 0;
        for (Column c : table.getColumns()) {
            fields.append((indexFields++ > 0 ? "," : "") + c.getName());
            if (c.isKey()) {
                order.append((indexOrder++ > 0 ? "," : "") + c.getName() + " asc");
            }
        }
        return "select " + fields + " from " + schema.getName() + "." + table.getName() + " order by " + order;
    }

    /**
     * Populate schema report.
     * 
     * @param schema
     *            The schema object.
     * @param filter
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
    public void populateTableReport(Schema schema, IFilter filter, SchemaReport report, Table table, ResultSet rsExpected, ResultSet rsReceived) throws SQLException {
        IResultEnumerator comp = getEnumerator(table, rsExpected, rsReceived);
        TableReport tr = new TableReport(table);
        while (comp.next()) {
            ResultSet exp = comp.getExpected();
            ResultSet rec = comp.getReceived();
            LineReport lr = null;
            int index = 0;
            if (exp == null && rec != null) {
                lr = new LineReport(RegisterType.ALIEN, tr);
                for (Column c : table.getColumns()) {
                    lr.add(c, index++, null, rec.getObject(c.getName()));
                }
                tr.add(lr);
            } else if (exp != null && rec == null) {
                lr = new LineReport(RegisterType.MISSING, tr);
                for (Column c : table.getColumns()) {
                    lr.add(c, index++, exp.getObject(c.getName()), null);
                }
                tr.add(lr);
            } else {
                lr = new LineReport(RegisterType.DIFFERENT, tr);
                for (Column c : table.getColumns()) {
                    if (!filter.accept(c)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Column ignored:" + c.getAlias() + "(" + c.getName() + ")");
                        }
                        continue;
                    }
                    Object objExp = exp.getObject(c.getName());
                    Object objRec = rec.getObject(c.getName());
                    if (!filter.accept(c, objRec)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Value ignored(" + c.getAlias() + "," + c.getName() + "):" + objRec);
                        }
                        continue;
                    }
                    boolean match = c.getComparator().match(objExp, objRec);
                    if (!c.isKey() && !match) {
                        lr.add(c, index++, objExp, objRec);
                    }
                }
                if (!lr.isEmpty()) {
                    for (Column c : table.getKeys()) {
                        lr.add(c, index++, exp.getObject(c.getName()), rec.getObject(c.getName()));
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
        return new ResultSetEnumerator(rsExpected, rsReceived, table.getKeys());
    }
}