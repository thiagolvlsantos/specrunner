/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.listeners;

import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;

/**
 * Generic plugin listener. Performs actions before and after plugin
 * <code>initialize()/doStart()/doEnd()</code> methods.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPluginListener extends ISpecRunnerListener {

    /**
     * Perform some action before plugin initialization.
     * 
     * @param plugin
     *            The plugin instance.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onBeforeInit(IPlugin plugin, IContext context, IResultSet result);

    /**
     * Perform some action after plugin initialization.
     * 
     * @param plugin
     *            The plugin instance.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onAfterInit(IPlugin plugin, IContext context, IResultSet result);

    /**
     * Perform some action before plugin start.
     * 
     * @param plugin
     *            The plugin instance.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onBeforeStart(IPlugin plugin, IContext context, IResultSet result);

    /**
     * Perform some action after plugin start.
     * 
     * @param plugin
     *            The plugin instance.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onAfterStart(IPlugin plugin, IContext context, IResultSet result);

    /**
     * Perform some action before plugin ending.
     * 
     * @param plugin
     *            The plugin instance.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onBeforeEnd(IPlugin plugin, IContext context, IResultSet result);

    /**
     * Perform some action after plugin ending.
     * 
     * @param plugin
     *            The plugin instance.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onAfterEnd(IPlugin plugin, IContext context, IResultSet result);
}
