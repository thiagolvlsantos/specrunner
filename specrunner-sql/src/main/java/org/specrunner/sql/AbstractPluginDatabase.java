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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginTable;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.sql.meta.Schema;
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
     * The schema name.
     */
    private String datasource;
    /**
     * The schema name.
     */
    private String schema;
    /**
     * The schema name.
     */
    private String database;
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
    public void setDatabase(String database) {
        this.database = database;
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
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        IDataSourceProvider datasource = PluginConnection.getProvider(context, getDatasource());
        Schema schema = PluginSchema.getSchema(context, getSchema());
        IDatabase database = PluginDatabase.getDatabase(context, getDatabase());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug(getClass().getSimpleName() + " datasource:" + datasource);
            UtilLog.LOG.debug(getClass().getSimpleName() + "     schema:" + schema);
            UtilLog.LOG.debug(getClass().getSimpleName() + "   database:" + database);
        }
        DataSource ds = datasource.getDataSource();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(getClass().getSimpleName() + " connection:" + connection);
            }
            database.perform(this, context, result, tableAdapter, connection, schema, mode);
        } catch (SQLException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.commit();
                }
            } catch (SQLException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
        }
        result.addResult(Success.INSTANCE, context.peek());
        return ENext.DEEP;
    }
}