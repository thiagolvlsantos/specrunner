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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.UtilNode;

/**
 * Logging profiler implementation.
 * 
 * @author Thiago Santos.
 * 
 */
public class ProfilerPluginListener implements IPluginListener {

    /**
     * Threshold to appear in log.
     */
    private static final int TRESHOLD = 5;
    /**
     * Stack of initialization times.
     */
    private final Stack<Long> timeInit = new Stack<Long>();
    /**
     * Stack of start times.
     */
    private final Stack<Long> timeStart = new Stack<Long>();
    /**
     * Stack of end times.
     */
    private final Stack<Long> timeEnd = new Stack<Long>();
    /**
     * Last init count.
     */
    private Long lastInit;
    /**
     * Last start count.
     */
    private Long lastStart;
    /**
     * Total init count.
     */
    private Long totalInit;
    /**
     * Total start count.
     */
    private Long totalStart;
    /**
     * Total end count.
     */
    private Long totalEnd;
    /**
     * Mapping of time by action types.
     */
    private Map<ActionType, Long> timeByType = new HashMap<ActionType, Long>();

    @Override
    public String getName() {
        return "profilerPlugin";
    }

    @Override
    public void reset() {
        timeInit.clear();
        timeStart.clear();
        timeEnd.clear();
        totalInit = 0L;
        totalStart = 0L;
        totalEnd = 0L;
        timeByType.clear();
    }

    @Override
    public void onBeforeInit(IPlugin plugin, IContext context, IResultSet result) {
        timeInit.push(System.currentTimeMillis());
    }

    @Override
    public void onAfterInit(IPlugin plugin, IContext context, IResultSet result) {
        lastInit = (System.currentTimeMillis() - timeInit.pop());
        if (UtilLog.LOG.isDebugEnabled() && lastInit > TRESHOLD) {
            UtilLog.LOG.debug("initialize(): " + lastInit + "mls. On " + context.getPlugin());
        }
        totalInit += lastInit;
    }

    @Override
    public void onBeforeStart(IPlugin plugin, IContext context, IResultSet result) {
        timeStart.push(System.currentTimeMillis());
    }

    @Override
    public void onAfterStart(IPlugin plugin, IContext context, IResultSet result) {
        lastStart = (System.currentTimeMillis() - timeStart.pop());
        if (UtilLog.LOG.isDebugEnabled() && lastStart > TRESHOLD) {
            UtilLog.LOG.debug("doStart(): " + lastStart + "mls. On " + context.getPlugin());
        }
        totalStart += lastStart;
    }

    @Override
    public void onBeforeEnd(IPlugin plugin, IContext context, IResultSet result) {
        timeEnd.push(System.currentTimeMillis());
    }

    @Override
    public void onAfterEnd(IPlugin plugin, IContext context, IResultSet result) {
        Long lastEnd = (System.currentTimeMillis() - timeEnd.pop());
        if (UtilLog.LOG.isDebugEnabled() && lastEnd > TRESHOLD) {
            UtilLog.LOG.debug("  doEnd(): " + lastEnd + "mls. On " + context.getPlugin());
        }
        totalEnd += lastEnd;
        IBlock peek = context.peek();
        if (UtilLog.LOG.isDebugEnabled() && peek.hasPlugin() && peek.getNode() instanceof Element) {
            IPlugin p = peek.getPlugin();
            ActionType actionType = p.getActionType();
            String name = p.getClass().getSimpleName();
            Long time = timeByType.get(actionType);
            if (time == null) {
                time = 0L;
            }
            timeByType.put(actionType, time + (lastInit + lastStart + lastEnd));

            Element e = (Element) peek.getNode();
            UtilNode.appendCss(e, "sr_time");
            e.addAttribute(new Attribute("srtime", (UtilLog.LOG.isTraceEnabled() ? name : "") + "\n(" + actionType.getName() + ")" + ":" + (lastInit > 0 ? lastInit : "") + "/" + (lastStart > 0 ? lastStart : "") + "/" + (lastEnd > 0 ? lastEnd : "")));
        }
    }

    /**
     * The mapping of time by action type.
     * 
     * @return The mapping.
     */
    public Map<ActionType, Long> getTimeByType() {
        return timeByType;
    }
}
