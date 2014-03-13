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

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.listeners.INodeListener;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.UtilNode;

/**
 * Monitor for scenarios.
 * 
 * @author Thiago Santos.
 * 
 */
public class ScenarioListener implements INodeListener {

    /**
     * Expected style for scenarios.
     */
    public static final String CSS_SCENARIO = "scenario";
    /**
     * Expected style for success scenarios.
     */
    public static final String CSS_SCENARIO_SUCCESS = "scenarioSuccess";
    /**
     * Expected style for failure scenarios.
     */
    public static final String CSS_SCENARIO_FAILURE = "scenarioFailure";
    /**
     * Expected style for pending scenarios.
     */
    public static final String CSS_SCENARIO_PENDING = "scenarioPending";
    /**
     * Expected style for scenario titles.
     */
    public static final String CSS_TITLE = "title";

    /**
     * The listener name.
     */
    protected String name;
    /**
     * An auxiliary node holder.
     */
    protected INodeHolder holder;
    /**
     * The node which holds the scenario.
     */
    protected Node scenario;
    /**
     * A checkpoint where in the general result the scenario starts.
     */
    protected int checkpoint;
    /**
     * Indicate if scenario is pending.
     */
    protected boolean pending = true;
    /**
     * The subset of general result for this scenario.
     */
    protected IResultSet subset;

    /**
     * Creates a names scenario listener. The scenario name must be the same of
     * the corresponding scenario it is expected to monitor.
     * 
     * @param name
     *            The scenario.
     */
    public ScenarioListener(String name) {
        this.name = name;
    }

    @Override
    public void reset() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ENext onBefore(Node node, IContext context, IResultSet result) {
        ENext next = ENext.DEEP;
        if (holder == null) {
            holder = UtilNode.newNodeHolder(node);
        } else {
            holder.setNode(node);
        }
        if (holder.attributeContains(UtilNode.ATT_CSS, CSS_SCENARIO)) {
            try {
                String title = UtilString.camelCase(UtilNode.getCssNode(node, CSS_TITLE).getValue(), true);
                if (name.equals(title)) {
                    this.scenario = node;
                    checkpoint = result.size();
                    pending = UtilNode.isPending(node);
                    if (pending) {
                        next = ENext.SKIP;
                    }
                }
            } catch (PluginException e) {
                throw new RuntimeException(e);
            }
        }
        return next;
    }

    @Override
    public void onAfter(Node node, IContext context, IResultSet result) {
        if (this.scenario == node) {
            this.subset = result.subSet(checkpoint, result.size());
            if (UtilNode.isPending(node)) {
                UtilNode.appendCss(node, CSS_SCENARIO_PENDING);
            } else if (subset.getStatus().isError()) {
                UtilNode.appendCss(node, CSS_SCENARIO_FAILURE);
            } else {
                UtilNode.appendCss(node, CSS_SCENARIO_SUCCESS);
            }
        }
    }

    /**
     * Answer if the scenario is pending.
     * 
     * @return true, if pending, false, otherwise.
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * Get the result subset specific to this scenario.
     * 
     * @return The result subset.
     */
    public IResultSet getResult() {
        return subset;
    }
}