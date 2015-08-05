/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.plugins.core;

import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.node.TableAdapter;
import org.specrunner.util.xom.node.UtilTable;

/**
 * Adapter for plugins on tables.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginTable extends AbstractPluginScoped {

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("initialize()>" + context.peek());
        }
        initialize(context, getTableAdapter(context));
    }

    /**
     * Gets the table adapter from a table.
     * 
     * @param context
     *            The context.
     * @return The table adapter.
     * @throws PluginException
     *             On creation errors.
     */
    protected TableAdapter getTableAdapter(IContext context) throws PluginException {
        Node element = context.getNode();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("ELEMENT>" + element);
        }
        TableAdapter table = null;
        if (!(element instanceof ParentNode)) {
            throw new PluginException("IPlugin " + getClass().getName() + " applicable only to parent elements.");
        } else {
            table = UtilTable.newTable(element);
        }
        if (!table.getQualifiedName().equalsIgnoreCase("table")) {
            throw new PluginException("IPlugin " + getClass().getName() + " applicable only to 'table' elements.");
        }
        return table;
    }

    /**
     * Initialize helper.
     * 
     * @param context
     *            The context.
     * @param table
     *            The adapter.
     * @throws PluginException
     *             On initialization errors.
     */
    public void initialize(IContext context, TableAdapter table) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("initialize(table)>" + context.peek());
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("doStart>" + context.peek());
        }
        return doStart(context, result, getTableAdapter(context));
    }

    /**
     * Perform the plugin start action.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param tableAdapter
     *            The adapter.
     * @return The next action to be taken.
     * @throws PluginException
     *             On plugin errors.
     */
    public ENext doStart(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("doStart(table)>" + context.peek());
        }
        return ENext.DEEP;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("doEnd>" + context.peek());
        }
        doEnd(context, result, getTableAdapter(context));
    }

    /**
     * Perform an ending action.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param tableAdapter
     *            The adapter.
     * @throws PluginException
     *             On plugin errors.
     */
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("doEnd(table)>" + context.peek());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
