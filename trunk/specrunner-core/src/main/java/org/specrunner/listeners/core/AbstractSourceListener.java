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
package org.specrunner.listeners.core;

import org.specrunner.context.IContext;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilLog;

/**
 * Helper for source listeners.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractSourceListener implements ISourceListener {

    @Override
    public void onBefore(ISource source, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBefore(" + context + "," + result + ")");
        }
    }

    @Override
    public void onAfter(ISource source, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfter(" + context + "," + result + ")");
        }
    }
}