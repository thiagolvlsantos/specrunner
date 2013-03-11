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
package org.specrunner.plugins.impl.logical;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginDual;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.UtilNode;

/**
 * Compare elements. Use class 'eq', with 'left' class to the first argument,
 * and 'right' class to the second argument.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginEquals extends AbstractPluginDual {

    /**
     * The CSS which set the left side condition of equals.
     */
    public static final String CSS_LETF = "left";
    /**
     * The CSS which set the right side condition of equals.
     */
    public static final String CSS_RIGHT = "right";

    /**
     * Error object after failure.
     */
    protected Throwable error;

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected boolean operation(Object obj, IContext context) throws PluginException {
        Node node = context.getNode();
        Nodes expectedes = node.query("descendant::*[@class='" + CSS_LETF + "']");
        Nodes receiveds = node.query("descendant::*[@class='" + CSS_RIGHT + "']");
        Node expected = UtilNode.getHighest(expectedes);
        Node received = UtilNode.getHighest(receiveds);
        if (expected == null) {
            throw new PluginException("Expected value not found. Missing a element with class='" + CSS_LETF + "' in element:" + node.toXML());
        }
        if (received == null) {
            throw new PluginException("Received value not found. Missing a element with class='" + CSS_RIGHT + "' in element:" + node.toXML());
        }
        String expectedValue = expected.getValue();
        if (received instanceof Element) {
            CellAdapter cell = new CellAdapter((Element) expected);
            if (cell.hasAttribute("value")) {
                expectedValue = cell.getAttribute("value");
            }
        }
        String receivedValue = received.getValue();
        if (received instanceof Element) {
            CellAdapter cell = new CellAdapter((Element) received);
            if (cell.hasAttribute("value")) {
                receivedValue = cell.getAttribute("value");
            }
        }

        Object objExpected = UtilEvaluator.evaluate(expectedValue, context);
        Object objReceived = UtilEvaluator.evaluate(receivedValue, context);
        try {
            return verify(objExpected, objReceived);
        } catch (SpecRunnerException e) {
            throw new PluginException(e);
        }
    }

    /**
     * Verify the condition.
     * 
     * @param reference
     *            The reference value.
     * @param value
     *            The received value.
     * @return true, if equals, false, otherwise.
     * @throws SpecRunnerException
     *             On condition errors.
     */
    protected boolean verify(Object reference, Object value) throws SpecRunnerException {
        boolean result = reference.equals(value);
        if (!result) {
            if (reference instanceof String && value instanceof String) {
                IStringAligner al = SpecRunnerServices.get(IStringAlignerFactory.class).align(reference.toString(), value.toString());
                error = new DefaultAlignmentException(al);
            } else {
                error = new PluginException("Values are different. Expected: '" + reference + "', Received: '" + value + "'.");
            }
        }
        return result;
    }

    @Override
    protected Throwable getError() {
        return error;
    }
}
