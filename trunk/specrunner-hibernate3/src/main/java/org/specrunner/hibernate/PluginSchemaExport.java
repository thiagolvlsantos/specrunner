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
package org.specrunner.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.specrunner.context.IContext;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;

/**
 * Creates a schema based on Hibernate annotated classes.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSchemaExport extends AbstractPlugin {

    /**
     * The configuration name.
     */
    private String configuration;
    /**
     * The factory name.
     */
    private String factory;
    /**
     * The schema name.
     */
    private String schema;
    /**
     * To show/generate script.
     */
    private boolean script = false;
    /**
     * To drop schema.
     */
    private boolean drop = true;
    /**
     * To export/update schema.
     */
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

    /**
     * Set the configuration.
     * 
     * @param configuration
     *            A configuration.
     */
    @DontEval
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

    /**
     * Set a session factory.
     * 
     * @param factory
     *            A factory.
     */
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
    @DontEval
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
     * true, if schema has to be dropped, false, otherwise. Default is true.
     * 
     * @return Schema drop mode.
     */
    public boolean isDrop() {
        return drop;
    }

    /**
     * Set the drop behavior.
     * 
     * @param drop
     *            The drop behavior flag.
     */
    public void setDrop(boolean drop) {
        this.drop = drop;
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
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Configuration cfg = PluginConfiguration.getConfiguration(context, getConfiguration());
        if (isDrop()) {
            SessionFactory sessionFactory = PluginSessionFactory.getSessionFactory(context, getFactory());
            dropSchema(context, sessionFactory);
            createSchema(context, sessionFactory);
        }
        try {
            new SchemaExport(cfg).create(isScript(), isExport());
            result.addResult(Success.INSTANCE, context.peek());
        } catch (HibernateException e) {
            result.addResult(Failure.INSTANCE, context.peek(), e);
        }
        return ENext.DEEP;
    }

    /**
     * Perform drop schema.
     * 
     * @param context
     *            The context.
     * @param sf
     *            The session factory.
     * @throws PluginException
     *             On drop errors.
     */
    protected void dropSchema(IContext context, SessionFactory sf) throws PluginException {
        Session s = null;
        try {
            s = sf.openSession();
            try {
                String str = "drop schema " + getSchema() + " cascade";
                SQLQuery sql = s.createSQLQuery(str);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(str);
                }
                sql.executeUpdate();
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Perform create schema.
     * 
     * @param context
     *            The context.
     * @param sf
     *            The session factory.
     * @throws PluginException
     *             On creation errors.
     */
    protected void createSchema(IContext context, SessionFactory sf) throws PluginException {
        Session s = null;
        try {
            s = sf.openSession();
            try {
                String str = "create schema " + getSchema() + " authorization dba";
                SQLQuery sql = s.createSQLQuery(str);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(str);
                }
                sql.executeUpdate();
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}