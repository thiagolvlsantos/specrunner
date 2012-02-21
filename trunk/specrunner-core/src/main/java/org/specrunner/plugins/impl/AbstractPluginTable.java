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
package org.specrunner.plugins.impl;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.parameters.impl.AbstractParametrized;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilNode;
import org.specrunner.util.impl.TableAdapter;

/**
 * Adapter for plugins on tables.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginTable extends AbstractParametrized implements IPlugin {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("initialize()>" + context.peek());
        }
        initialize(context, getTableAdapter(context));
    }

    private TableAdapter getTableAdapter(IContext context) throws PluginException {
        Node element = context.getNode();
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("ELEMENT>" + element);
        }
        TableAdapter table = null;
        if (!(element instanceof ParentNode)) {
            throw new PluginException("IPlugin " + getClass().getName() + " applicable only to parent elements.");
        } else {
            table = UtilNode.newTableAdapter((Element) element);
        }
        if (!table.getElement().getQualifiedName().equalsIgnoreCase("table")) {
            throw new PluginException("IPlugin " + getClass().getName() + " applicable only to 'tables' elements.");
        }
        return table;
    }

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
