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
package org.specrunner.plugins.core.logical;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SpecRunnerException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.expressions.Unsilent;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginDual;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.UtilNode;

/**
 * Compare elements. Use class 'eq', there are two approaches:
 * <ul>
 * <li>Add two classes with 'left' class to the first argument, and 'right'
 * class to the second argument.</li>
 * <li>Value attribute is compared with tag content.</li>
 * 
 * </ul>
 * In both comparisons a specific comparator can be set using attribute
 * <code>comparator</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginEquals extends AbstractPluginDual {

    /**
     * Error object after failure.
     */
    protected Throwable error;

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    @Unsilent
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    protected boolean operation(Object obj, IContext context) throws PluginException {
        Node node = context.getNode();
        INodeHolder parent = UtilNode.newNodeHolder(node);
        Object objExpected = null;
        Object objReceived = null;
        if (node instanceof Element) {
            if (parent.hasAttribute("value") || parent.hasAttribute("property")) {
                Object tmp = parent.getObject(context, false);
                if (obj instanceof String) {
                    objExpected = getNormalized(String.valueOf(tmp));
                    objReceived = getNormalized(String.valueOf(obj));
                } else {
                    objExpected = tmp;
                    objReceived = obj;
                }
            } else {
                objExpected = UtilNode.newNodeHolder(UtilNode.getLeft(node)).getObject(context, true);
                objReceived = UtilNode.newNodeHolder(UtilNode.getRight(node)).getObject(context, true);
            }
        }
        try {
            return verify(context, parent.getComparator(), objExpected, objReceived);
        } catch (SpecRunnerException e) {
            throw new PluginException(e);
        }
    }

    /**
     * Verify the condition.
     * 
     * @param context
     *            The context.
     * @param comparator
     *            Comparator.
     * @param expected
     *            The reference value.
     * @param received
     *            The received value.
     * @return true, if equals, false, otherwise.
     * @throws SpecRunnerException
     *             On condition errors.
     */
    protected boolean verify(IContext context, IComparator comparator, Object expected, Object received) throws SpecRunnerException {
        boolean result = comparator.match(expected, received);
        if (!result) {
            if (expected instanceof String && received instanceof String) {
                error = new DefaultAlignmentException(expected.toString(), received.toString());
            } else {
                error = new PluginException("Values are different. Expected: '" + expected + "', Received: '" + received + "'. \n" + (context.hasNode() ? "Node:" + context.getNode().toXML() : "Plugin:" + context.getPlugin()));
            }
        }
        return result;
    }

    @Override
    protected Throwable getError() {
        return error;
    }
}
