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
     * Expected style for scenario titles.
     */
    public static final String CSS_TITLE = "title";

    /**
     * The listener name.
     */
    private String name;
    /**
     * An auxiliary node holder.
     */
    private INodeHolder holder;
    /**
     * The node which holds the scenario.
     */
    private Node scenario;
    /**
     * A checkpoint where in the general result the scenario starts.
     */
    private int checkpoint;
    /**
     * The subset of general result for this scenario.
     */
    private IResultSet subset;

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
                }
            } catch (PluginException e) {
                throw new RuntimeException(e);
            }
        }
        return ENext.DEEP;
    }

    @Override
    public void onAfter(Node node, IContext context, IResultSet result) {
        if (this.scenario == node) {
            this.subset = result.subSet(checkpoint, result.size());
        }
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