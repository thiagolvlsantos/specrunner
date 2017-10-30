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
package org.specrunner.listeners.core;

import org.specrunner.context.IContext;
import org.specrunner.listeners.INodeListener;
import org.specrunner.plugins.ENext;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

import nu.xom.Node;

/**
 * Helper for node listeners.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractNodeListener implements INodeListener {

    @Override
    public ENext onBefore(Node node, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBefore(" + context + "," + result + ")");
        }
        return ENext.DEEP;
    }

    @Override
    public void onAfter(Node node, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfter(" + context + "," + result + ")");
        }
    }
}
