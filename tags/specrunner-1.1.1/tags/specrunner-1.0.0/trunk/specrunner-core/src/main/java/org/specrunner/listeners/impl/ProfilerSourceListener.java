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
package org.specrunner.listeners.impl;

import java.util.Stack;

import org.specrunner.context.IContext;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

/**
 * Logging profiler implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ProfilerSourceListener implements ISourceListener {

    private Stack<Long> timeStart = new Stack<Long>();

    @Override
    public String getName() {
        return "profilerSource";
    }

    @Override
    public void onBefore(IContext context, IResultSet result) {
        timeStart.push(System.currentTimeMillis());
    }

    @Override
    public void onAfter(IContext context, IResultSet result) {
        long time = (System.currentTimeMillis() - timeStart.pop());
        if (UtilLog.LOG.isInfoEnabled() && time > 0) {
            UtilLog.LOG.info("Source '" + context.getSources().peek().getString() + "' performed in " + time + "mls.");
        }
    }

}
