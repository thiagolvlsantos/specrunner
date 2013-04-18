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
import java.util.Map.Entry;

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
import org.specrunner.sql.ReportException.TableReport;
import org.specrunner.sql.ReportException.TableReport.LineReport;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.UtilLog;

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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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
        ReportException report = new ReportException();
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
                TableReport tr = new TableReport(table);

                StringBuilder fields = new StringBuilder();
                StringBuilder order = new StringBuilder();
                int indexFields = 0;
                int indexOrder = 0;
                for (Entry<String, Column> ec : table.getAliasToColumns().entrySet()) {
                    Column c = ec.getValue();
                    fields.append((indexFields++ > 0 ? "," : "") + c.getName());
                    if (c.isKey()) {
                        order.append((indexOrder++ > 0 ? "," : "") + c.getName() + " asc");
                    }
                }
                String sql = "select " + fields + " from " + table.getSchema().getName() + "." + table.getName() + " order by " + order;
                ResultSet rsExpected = stmtExpected.executeQuery(sql);
                ResultSet rsReceived = stmtReceived.executeQuery(sql);
                ResultSetEnumerator comp = new ResultSetEnumerator(rsExpected, rsReceived, table.getKeys());
                while (comp.next()) {
                    ResultSet exp = comp.expected();
                    ResultSet rec = comp.received();
                    LineReport lr = null;
                    int index = 0;
                    if (exp == null && rec != null) {
                        lr = new LineReport(LineReport.Type.ALIEN, table);
                        for (Column c : table.getColumns()) {
                            lr.indexes.put(c.getName(), index++);
                            lr.cols.add(c);
                            lr.rec.add(rec.getObject(c.getName()));
                        }
                        tr.lines.add(lr);
                    } else if (exp != null && rec == null) {
                        lr = new LineReport(LineReport.Type.MISSING, table);
                        for (Column c : table.getColumns()) {
                            lr.indexes.put(c.getName(), index++);
                            lr.cols.add(c);
                            lr.exp.add(exp.getObject(c.getName()));
                        }
                        tr.lines.add(lr);
                    } else {
                        lr = new LineReport(LineReport.Type.DIFFERENT, table);
                        for (Column c : table.getColumns()) {
                            Object objExp = exp.getObject(c.getName());
                            Object objRec = rec.getObject(c.getName());
                            boolean match = c.getComparator().match(objExp, objRec);
                            if (!c.isKey() && !match) {
                                lr.indexes.put(c.getName(), index++);
                                lr.cols.add(c);
                                lr.exp.add(objExp);
                                lr.rec.add(objRec);
                            }
                        }
                        if (!lr.cols.isEmpty()) {
                            for (Column c : table.getKeys()) {
                                Object objExp = exp.getObject(c.getName());
                                Object objRec = rec.getObject(c.getName());
                                lr.indexes.put(c.getName(), index++);
                                lr.cols.add(c);
                                lr.exp.add(objExp);
                                lr.rec.add(objRec);
                            }
                            tr.lines.add(lr);
                        }
                    }
                }
                if (!tr.lines.isEmpty()) {
                    report.tabl.add(tr);
                }
                rsExpected.close();
                rsReceived.close();
            }
        } catch (SQLException e) {
            throw new PluginException(e);
        } finally {
            try {
                stmtExpected.close();
            } catch (SQLException e) {
                throw new PluginException(e);
            } finally {
                try {
                    stmtReceived.close();
                } catch (SQLException e) {
                    throw new PluginException(e);
                }
            }
        }
        if (!report.isEmpty()) {
            result.addResult(Failure.INSTANCE, context.peek(), report);
        } else {
            result.addResult(Success.INSTANCE, context.peek());
        }
        return ENext.DEEP;
    }
}