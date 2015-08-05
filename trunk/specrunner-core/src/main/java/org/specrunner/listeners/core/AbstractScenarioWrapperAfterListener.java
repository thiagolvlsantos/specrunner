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

import nu.xom.Node;

import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;

/**
 * Wrapp a scenario with a end tag.
 * 
 * @author Thiago Santos
 */
public abstract class AbstractScenarioWrapperAfterListener extends AbstractScenarioWrapperListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
        append(node, context, result, getOnEnd(), getOnEndMessage(), false);
    }

    @Override
    protected Class<? extends IPlugin> getOnStart() {
        return null;
    }

    @Override
    protected String getOnStartMessage() {
        return null;
    }
}
