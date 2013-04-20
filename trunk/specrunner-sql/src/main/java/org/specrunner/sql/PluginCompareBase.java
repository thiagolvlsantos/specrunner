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
import org.specrunner.sql.report.LineReport;
import org.specrunner.sql.report.RegisterType;
import org.specrunner.sql.report.ReportException;
import org.specrunner.sql.report.SchemaReport;
import org.specrunner.sql.report.TableReport;
import org.specrunner.util.UtilLog;

/**
 * Provided a <code>Schema</code> instance, and to databases, this plugin
 * compare these database. The database used by the system under test name is '
 * <code>system</code>' and the reference database is '<code>reference</code>'.
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
     * Feature for table[cols] ignore.
     */
    public static final String FEATURE_SKIP = PluginCompareBase.class.getName() + ".skip";
    /**
     * Pattern to ignore some tables and(or) fields.
     */
    private String skip;

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
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Get the skip expression. i.e. '*' skips every table; 'REC*', all tables
     * starting with REC; '~REC*', all tables not starting with 'REC';
     * 'USER[*_ID]', table user where fields end with ID, and so on.
     * 
     * @return The expression.
     */
    public String getSkip() {
        return skip;
    }

    /**
     * Set the skip expression.
     * 
     * @param skip
     *            Expression.
     */
    public void setSkip(String skip) {
        this.skip = skip;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.set(FEATURE_SCHEMA, this);
        fh.set(FEATURE_SYSTEM, this);
        fh.set(FEATURE_REFERENCE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Schema schema = PluginSchema.getSchema(context, getSchema());
        IDataSourceProvider expected = PluginConnection.getProvider(context, getReference());
        IDataSourceProvider received = PluginConnection.getProvider(context, getSystem());
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("     Schema provider:" + schema);
            UtilLog.LOG.info("   Datasource system:" + received);
            UtilLog.LOG.info("Datasource reference:" + expected);
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
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(getClass().getSimpleName() + " connection expected:" + connectionExpected);
                UtilLog.LOG.info(getClass().getSimpleName() + " connection received:" + connectionReceived);
                UtilLog.LOG.info(getClass().getSimpleName() + "  statement expected:" + stmtExpected);
                UtilLog.LOG.info(getClass().getSimpleName() + "  statement received:" + stmtReceived);
            }
            for (Table table : schema.getTables()) {
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
                String sql = "select " + fields + " from " + table.getSchema().getName() + "." + table.getName() + " order by " + order;
                ResultSet rsExpected = null;
                ResultSet rsReceived = null;
                try {
                    rsExpected = stmtExpected.executeQuery(sql);
                    rsReceived = stmtReceived.executeQuery(sql);
                    populateReport(report, table, rsExpected, rsReceived);
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new PluginException(e);
                } finally {
                    try {
                        rsExpected.close();
                    } catch (Exception e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        throw new PluginException(e);
                    } finally {
                        try {
                            rsReceived.close();
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
                stmtExpected.close();
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            } finally {
                try {
                    stmtReceived.close();
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
     * Populate schema report.
     * 
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
    protected void populateReport(SchemaReport report, Table table, ResultSet rsExpected, ResultSet rsReceived) throws SQLException {
        ResultSetEnumerator comp = new ResultSetEnumerator(rsExpected, rsReceived, table.getKeys());
        TableReport tr = new TableReport(table);
        while (comp.next()) {
            ResultSet exp = comp.expected();
            ResultSet rec = comp.received();
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
                    Object objExp = exp.getObject(c.getName());
                    Object objRec = rec.getObject(c.getName());
                    boolean match = c.getComparator().match(objExp, objRec);
                    if (!c.isKey() && !match) {
                        lr.add(c, index++, objExp, objRec);
                    }
                }
                if (!lr.isEmpty()) {
                    for (Column c : table.getKeys()) {
                        Object objExp = exp.getObject(c.getName());
                        Object objRec = rec.getObject(c.getName());
                        lr.add(c, index++, objExp, objRec);
                    }
                    tr.add(lr);
                }
            }
        }
        if (!tr.isEmpty()) {
            report.add(tr);
        }
    }
}