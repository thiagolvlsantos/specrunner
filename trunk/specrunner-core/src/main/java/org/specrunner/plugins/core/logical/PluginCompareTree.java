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

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.SRServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.core.PluginAssertion;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.core.include.PluginInclude;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.UtilNode;

/**
 * Perform a comparison of nodes.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareTree extends AbstractPlugin {

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        Node received = UtilNode.getLeft(node);
        Node expected = UtilNode.getRight(node);
        String alias = SRServices.get(IPluginFactory.class).getAlias(PluginInclude.class);
        if (received instanceof Element && UtilNode.newNodeHolder(received).attributeContains("class", alias)) {
            received = received.getParent().getChild(received.getParent().indexOf(received) + 1);
        }
        if (expected instanceof Element && UtilNode.newNodeHolder(expected).attributeContains("class", alias)) {
            expected = expected.getParent().getChild(expected.getParent().indexOf(expected) + 1);
        }
        compare(context, result, received, expected);
    }

    /**
     * Compare two nodes.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param received
     *            The received node.
     * @param expected
     *            The expected node.
     */
    protected void compare(IContext context, IResultSet result, Node received, Node expected) {
        boolean ignore = UtilNode.isIgnore(expected);
        boolean leaves = isLeaf(received) && isLeaf(expected);
        if (leaves) {
            if (ignore) {
                return;
            }
            Object objReceived = null;
            Object objExpected = null;
            IComparator comparator = null;
            if (received instanceof Element && expected instanceof Element) {
                INodeHolder holderReceived = UtilNode.newNodeHolder(received);
                INodeHolder holderExpected = UtilNode.newNodeHolder(expected);
                try {
                    IConverter converter = holderExpected.getConverter();
                    List<String> arguments = holderExpected.getArguments();
                    objReceived = holderReceived.getObject(context, true, converter, arguments);
                    objExpected = holderExpected.getObject(context, true, converter, arguments);
                    comparator = holderExpected.getComparator();
                } catch (ConverterException e) {
                    result.addResult(Failure.INSTANCE, context.newBlock(expected, this), e);
                } catch (PluginException e) {
                    result.addResult(Failure.INSTANCE, context.newBlock(expected, this), e);
                } catch (ComparatorException e) {
                    result.addResult(Failure.INSTANCE, context.newBlock(expected, this), e);
                }
            } else {
                objReceived = received.getValue();
                objExpected = expected.getValue();
                comparator = SRServices.getComparatorManager().getDefault();
            }
            try {
                UtilPlugin.compare(expected, PluginAssertion.INSTANCE, result, comparator, objExpected, objReceived);
            } catch (PluginException e) {
                result.addResult(Failure.INSTANCE, context.newBlock(expected, this), e);
            }
        }
        if (!ignore && received.getChildCount() != expected.getChildCount()) {
            result.addResult(Failure.INSTANCE, context.newBlock(expected, this), new PluginException("Different number of children nodes. Expected:" + expected.toXML() + ", Received:" + received.toXML()));
        } else {
            if (!leaves) {
                for (int i = 0; i < received.getChildCount(); i++) {
                    compare(context, result, received.getChild(i), expected.getChild(i));
                }
            }
        }
    }

    /**
     * Check if a node is leaf, text, or parent of text only.
     * 
     * @param root
     *            The root.
     * @return true, if leaf, false, otherwise.
     */
    private boolean isLeaf(Node root) {
        return (root instanceof Text) || (root instanceof Element && root.getChildCount() == 1 && root.getChild(0) instanceof Text);
    }
}
