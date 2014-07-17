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
package org.specrunner.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.util.StringUtil;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.TableAdapter;

/**
 * Given a data source, a schema and a table, perform operations.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginDatabase extends AbstractPluginTable {

    /**
     * Feature for schema name.
     */
    public static final String FEATURE_SCHEMA = AbstractPluginDatabase.class.getName() + ".schema";
    /**
     * The schema name.
     */
    private String schema;
    /**
     * Feature for datasource names.
     */
    public static final String FEATURE_DATASOURCE = AbstractPluginDatabase.class.getName() + ".datasource";
    /**
     * The schema name.
     */
    private String datasource;
    /**
     * Feature for datasource names.
     */
    public static final String FEATURE_DATABASE = AbstractPluginDatabase.class.getName() + ".database";
    /**
     * The schema name.
     */
    private String database;
    /**
     * Feature for names separators.
     */
    public static final String FEATURE_SEPARATOR = AbstractPluginDatabase.class.getName() + ".separator";
    /**
     * Default separator.
     */
    public static final String DEFAULT_SEPARATOR = ";";
    /**
     * The separator, default is ";".
     */
    private String separator = DEFAULT_SEPARATOR;
    /**
     * The database action mode.
     */
    private EMode mode;

    /**
     * A mode is required.
     * 
     * @param mode
     *            A mode.
     */
    protected AbstractPluginDatabase(EMode mode) {
        this.mode = mode;
    }

    /**
     * Gets the datasource name.
     * 
     * @return The name.
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * Set the database name.
     * 
     * @param datasource
     *            A new datasource name.
     */
    @DontEval
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    /**
     * Gets schema name.
     * 
     * @return The schema name.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the schema name.
     * 
     * @param schema
     *            A new schema name.
     */
    @DontEval
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * The database provider.
     * 
     * @return The database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Set the database instance.
     * 
     * @param database
     *            The database.
     */
    @DontEval
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Get the name separator.
     * 
     * @return The names separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Set the name separator.
     * 
     * @param separator
     *            The separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Get the plugin mode.
     * 
     * @return The mode.
     */
    public EMode getMode() {
        return mode;
    }

    /**
     * Set the plugin mode.
     * 
     * @param mode
     *            A new mode.
     */
    public void setMode(EMode mode) {
        this.mode = mode;
    }

    @Override
    public void initialize(IContext context, TableAdapter table) throws PluginException {
        super.initialize(context, table);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (schema == null) {
            fm.set(FEATURE_SCHEMA, this);
        }
        if (datasource == null) {
            fm.set(FEATURE_DATASOURCE, this);
        }
        if (database == null) {
            fm.set(FEATURE_DATABASE, this);
        }
        fm.set(FEATURE_SEPARATOR, this);
    }

    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        Schema schema = PluginSchema.getSchema(context, getSchema());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(getClass().getSimpleName() + "     schema(" + getSchema() + "):" + schema);
        }
        String[] sources = StringUtil.tokenize(getDatasource() != null ? getDatasource() : PluginConnection.DEFAULT_CONNECTION_NAME, separator);
        String[] bases = StringUtil.tokenize(getDatabase() != null ? getDatabase() : PluginDatabase.DEFAULT_DATABASE_NAME, separator);
        int erros = 0;
        for (int i = 0; i < sources.length && i < bases.length; i++) {
            String source = sources[i];
            String base = bases[i];
            IDataSourceProvider datasource = PluginConnection.getProvider(context, source);
            IDatabase database = PluginDatabase.getDatabase(context, base);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(getClass().getSimpleName() + " datasource(" + source + "):" + datasource);
                UtilLog.LOG.debug(getClass().getSimpleName() + "   database(" + base + "):" + database);
            }
            DataSource ds = datasource.getDataSource();
            Connection connection = null;
            try {
                connection = ds.getConnection();
                if (UtilLog.LOG.isDebugEnabled()) {
                    DatabaseMetaData metaData = connection.getMetaData();
                    UtilLog.LOG.debug(getClass().getSimpleName() + " connection:(" + metaData.getURL() + ")" + connection);
                }
                database.perform(context, result, tableAdapter, connection, schema, mode);
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                erros++;
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Error in datasource: " + source + ", and database: " + base + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
            } catch (PluginException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                erros++;
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Error in datasource: " + source + ", and database: " + base + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
            } finally {
                try {
                    if (connection != null) {
                        connection.commit();
                    }
                } catch (SQLException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    erros++;
                    result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Error in datasource: " + source + ", and database: " + base + ", and schema: " + schema.getAlias() + ". Error: " + e.getMessage(), e));
                }
            }
        }
        if (erros == 0) {
            result.addResult(Success.INSTANCE, context.peek());
        }
    }
}