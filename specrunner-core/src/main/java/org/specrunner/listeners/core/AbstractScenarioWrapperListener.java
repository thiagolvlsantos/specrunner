/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.annotations.IScenarioListener;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.UtilNode;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

/**
 * Wrapp a scenario with a start tag, and an end tag, i.e. clean database (dbms)
 * and compare database (compareBase).
 * 
 * @author Thiago Santos
 */
public abstract class AbstractScenarioWrapperListener implements IScenarioListener {

    @Override
    public void beforeScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
        append(node, context, result, getOnStart(), getOnStartMessage(), true);
        append(node, context, result, getOnEnd(), getOnEndMessage(), false);
    }

    /**
     * Append a start and and tag to a scenario. If 'before' attribute is set to
     * 'false' none listener will be added on begin, if 'after' attribute is set
     * to 'false' none listener will be added on end.
     * 
     * @param node
     *            Scenario node.
     * @param context
     *            A context.
     * @param result
     *            A result set.
     * @param type
     *            A plugin type to add.
     * @param message
     *            A message to add.
     * @param onStart
     *            true, if add on start scenario, false, otherwise.
     * @throws PluginException
     *             On processing errors.
     */
    protected void append(Node node, IContext context, IResultSet result, Class<? extends IPlugin> type, String message, boolean onStart) throws PluginException {
        if (node instanceof Element) {
            Element element = (Element) node;
            Attribute before = element.getAttribute(BEFORE);
            if (before != null && onStart && !Boolean.valueOf(before.getValue())) {
                return;
            }
            Attribute after = element.getAttribute(AFTER);
            if (after != null && !onStart && !Boolean.valueOf(after.getValue())) {
                return;
            }
        }

        Node n = null;
        Nodes ns = node.query("child::td");
        if (ns.size() == 0) {
            n = node.getChild(onStart ? 0 : node.getChildCount() - 1);
        } else {
            n = ns.get(onStart ? 0 : ns.size() - 1);
        }
        Element s = createElement(type, message, onStart);
        if (n instanceof Element) {
            Element element = (Element) n;
            if (onStart) {
                element.insertChild(s, 0);
            } else {
                element.appendChild(s);
            }
        } else {
            ParentNode parent = n.getParent();
            if (onStart) {
                parent.insertChild(s, 0);
            } else {
                parent.appendChild(s);
            }
        }
    }

    /**
     * On start plugin type.
     * 
     * @return A type.
     */
    protected abstract Class<? extends IPlugin> getOnStart();

    /**
     * On start message.
     * 
     * @return A message.
     */
    protected abstract String getOnStartMessage();

    /**
     * On end plugin type.
     * 
     * @return A type.
     */
    protected abstract Class<? extends IPlugin> getOnEnd();

    /**
     * Get on end message.
     * 
     * @return A message.
     */
    protected abstract String getOnEndMessage();

    /**
     * Create element to add.
     * 
     * @param type
     *            Plugin type.
     * @param message
     *            A message.
     * @param onStart
     *            If it is start or end.
     * @return An element.
     * @throws PluginException
     *             O creation errors.
     */
    protected Element createElement(Class<? extends IPlugin> type, String message, boolean onStart) throws PluginException {
        Element s = new Element("span");
        UtilNode.appendCss(s, SRServices.get(IPluginFactory.class).getAlias(type));
        s.appendChild("[" + message + "]");
        return s;
    }

    @Override
    public void afterScenario(String title, Node node, IContext context, IResultSet result, Object instance) throws SpecRunnerException {
    }
}
