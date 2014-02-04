/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;

/**
 * Creates a schema based on Hibernate annotated classes.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSchema extends AbstractPlugin {

    private String configuration;
    private String factory;
    private String schema;
    private boolean script = false;
    private boolean export = true;

    /**
     * Name of configuration to be used.
     * 
     * @return The configuration name, if not set
     *         PluginConnection.SESSION_CONFIGURATION will be used.
     */
    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    /**
     * Name of session factory to be used.
     * 
     * @return The session factory name, if not set
     *         PluginConnection.SESSION_FACTORY will be used.
     */
    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    /**
     * Gets the schema name.
     * 
     * @return The schema name.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the default schema.
     * 
     * @param schema
     *            The schema.
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * true, if script must be writen to <code>System.out</code>. Default is
     * 'false'.
     * 
     * @return true, if show script, false, otherwise.
     */
    public boolean isScript() {
        return script;
    }

    /**
     * Set script output status.
     * 
     * @param script
     *            true, for show, false, otherwise.
     */
    public void setScript(boolean script) {
        this.script = script;
    }

    /**
     * true, to perform schema creation on database, false, otherwise. Default
     * is 'true'.
     * 
     * @return true, if export is enabled, false, otherwise.
     */
    public boolean isExport() {
        return export;
    }

    /**
     * Sets export status.
     * 
     * @param export
     *            The export status.
     */
    public void setExport(boolean export) {
        this.export = export;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Configuration cfg = PluginConfiguration.getConfiguration(context, getConfiguration());
        SessionFactory sf = PluginSessionFactory.getSessionFactory(context, getFactory());
        Session s = null;
        try {
            s = sf.openSession();
            String str = null;
            SQLQuery sql = null;
            try {
                str = "drop schema " + getSchema() + " cascade";
                // clear schema
                sql = s.createSQLQuery(str);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(str);
                }
                sql.executeUpdate();
            } catch (Exception e) {
                if (UtilLog.LOG.isErrorEnabled()) {
                    UtilLog.LOG.error(str, e);
                }
            }
            try {
                str = "create schema " + getSchema() + " authorization dba";
                sql = s.createSQLQuery(str);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(str);
                }
                sql.executeUpdate();
            } catch (Exception e) {
                if (UtilLog.LOG.isErrorEnabled()) {
                    UtilLog.LOG.error(str, e);
                }
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }
        try {
            new SchemaExport(cfg).create(isScript(), isExport());
            result.addResult(Status.SUCCESS, context.peek());
        } catch (HibernateException e) {
            result.addResult(Status.FAILURE, context.peek(), e);
        }
        return ENext.DEEP;
    }
}
