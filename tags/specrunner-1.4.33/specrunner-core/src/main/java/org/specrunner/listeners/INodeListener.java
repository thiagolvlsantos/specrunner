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
package org.specrunner.listeners;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.result.IResultSet;

/**
 * Generic node listener. Performs actions before and after plugin
 * <code>IRunner.run(...)</code> methods.
 * 
 * @author Thiago Santos
 * 
 */
public interface INodeListener extends ISpecRunnerListener {

    /**
     * Perform some action before building plugin for a given node.
     * 
     * @param node
     *            The node.
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @return The next step on execution. If return is <code>ENext.DEEP</code>,
     *         the execution should go deeper.
     */
    ENext onBefore(Node node, IContext context, IResultSet result);

    /**
     * Perform some action after plugin execution for a given node.
     * 
     * @param node
     *            The node.
     * @param context
     *            The context.
     * @param result
     *            The result.
     */
    void onAfter(Node node, IContext context, IResultSet result);
}