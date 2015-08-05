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
package org.specrunner.listeners.core;

import org.specrunner.context.IContext;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

/**
 * Helper for plugin listeners.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginListener implements IPluginListener {

    @Override
    public void onBeforeInit(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBeforeInit(" + result + ")");
        }
    }

    @Override
    public void onAfterInit(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfterInit(" + result + ")");
        }
    }

    @Override
    public void onBeforeStart(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBeforeStart(" + result + ")");
        }
    }

    @Override
    public void onAfterStart(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfterStart(" + result + ")");
        }
    }

    @Override
    public void onBeforeEnd(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBeforeEnd(" + result + ")");
        }
    }

    @Override
    public void onAfterEnd(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfterEnd(" + result + ")");
        }
    }

}
