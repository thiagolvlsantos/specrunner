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
package org.specrunner.plugins;

import org.specrunner.context.IContext;
import org.specrunner.parameters.IParametrized;
import org.specrunner.result.IResultSet;

/**
 * Generic plugin definition, stand for the action element of the framework.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPlugin extends IParametrized {

    /**
     * Initialize the plugin. Performed after set parameters.
     * 
     * @param context
     *            The test context.
     * @throws PluginException
     *             On initialization errors.
     */
    void initialize(IContext context) throws PluginException;

    /**
     * Performs the start action. i.e. create a database schema. Performed after
     * initialization.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @return SKIP, to skip node, DEEP, to go deep into the node.
     * @throws PluginException
     *             On execution errors.
     */
    ENext doStart(IContext context, IResultSet result) throws PluginException;

    /**
     * The end action. i.e. after a loop specification perform something in a
     * newly generated table. Performed after doStart().
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @throws PluginException
     *             On execution errors.
     */
    void doEnd(IContext context, IResultSet result) throws PluginException;
}
