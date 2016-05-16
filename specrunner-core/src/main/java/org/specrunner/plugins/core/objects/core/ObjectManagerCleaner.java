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
package org.specrunner.plugins.core.objects.core;

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.result.IResultSet;

import nu.xom.Node;

/**
 * Listener to clean object memory information.
 * 
 * @author Thiago Santos
 * 
 */
public class ObjectManagerCleaner implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
        SRServices.getObjectManager().clear();
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
    }
}
