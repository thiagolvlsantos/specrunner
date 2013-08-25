package org.specrunner.plugins.impl.logical;

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.plugins.impl.include.PluginInclude;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.comparer.ComparatorException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.xom.IElementHolder;
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
        String alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginInclude.class);
        if (received instanceof Element && ((Element) received).getAttribute("class").getValue().contains(alias)) {
            received = received.getParent().getChild(received.getParent().indexOf(received) + 1);
        }
        if (expected instanceof Element && ((Element) expected).getAttribute("class").getValue().contains(alias)) {
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
                IElementHolder holderReceived = UtilNode.newElementAdapter(received);
                IElementHolder holderExpected = UtilNode.newElementAdapter(expected);
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
                comparator = SpecRunnerServices.get(IComparatorManager.class).getDefault();
            }
            try {
                UtilPlugin.compare(expected, result, comparator, objExpected, objReceived);
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
