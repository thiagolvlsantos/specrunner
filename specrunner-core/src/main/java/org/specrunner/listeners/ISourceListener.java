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
package org.specrunner.listeners;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;

/**
 * A generic source listener.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISourceListener extends ISpecRunnerListener {

    /**
     * To be performed before start a source execution.
     * 
     * @param source
     *            The specification.
     * @param context
     *            A context.
     * @param result
     *            A result set.
     */
    void onBefore(ISource source, IContext context, IResultSet result);

    /**
     * To be performed after a source execution.
     * 
     * @param source
     *            The specification.
     * @param context
     *            A context.
     * @param result
     *            A result set.
     */
    void onAfter(ISource source, IContext context, IResultSet result);
}
