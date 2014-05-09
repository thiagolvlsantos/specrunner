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
package org.specrunner.listeners;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;

/**
 * Listeners for scenarios.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IScenarioListener {

    /**
     * Perform something before each scenario.
     * 
     * @param title
     *            The scenario title.
     * @param node
     *            The scenario node.
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     */
    void beforeScenario(String title, Node node, IContext context, IResultSet result);

    /**
     * Perform something after each scenario.
     * 
     * @param title
     *            The scenario title.
     * @param node
     *            The scenario node.
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     */
    void afterScenario(String title, Node node, IContext context, IResultSet result);
}
