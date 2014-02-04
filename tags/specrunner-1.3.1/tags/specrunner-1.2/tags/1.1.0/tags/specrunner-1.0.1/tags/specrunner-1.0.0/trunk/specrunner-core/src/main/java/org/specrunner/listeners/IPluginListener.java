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
package org.specrunner.listeners;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;

/**
 * Generic plugin listener. Performs actions before and after plugin
 * <code>initialize()/doStart()/doEnd()</code> methods.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPluginListener extends ISpecRunnerListener {

    void onBeforeInit(IContext context, IResultSet result);

    void onAfterInit(IContext context, IResultSet result);

    void onBeforeStart(IContext context, IResultSet result);

    void onAfterStart(IContext context, IResultSet result);

    void onBeforeEnd(IContext context, IResultSet result);

    void onAfterEnd(IContext context, IResultSet result);
}
