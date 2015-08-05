/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.htmlunit.assertions;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.SRServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.string.IStringNormalizer;
import org.specrunner.util.string.UtilString;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Comparison utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class PluginCompareUtils {

    /**
     * Hidden constructor.
     */
    private PluginCompareUtils() {
    }

    /**
     * Compare two strings.
     * 
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     * @param block
     *            The block.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param page
     *            The client page.
     * @return true, if equals, false, otherwise.
     * @throws PluginException
     *             On comparison errors.
     */
    public static boolean compare(String expected, String received, IBlock block, IContext context, IResultSet result, SgmlPage page) throws PluginException {
        boolean res = true;
        if (expected.equals(received)) {
            result.addResult(Success.INSTANCE, block);
        } else {
            addError(expected, received, block, context, result, page);
            res = false;
        }
        return res;
    }

    /**
     * Add error to result.
     * 
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     * @param block
     *            The block.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param page
     *            The page.
     * @throws PluginException
     *             On errors.
     */
    protected static void addError(String expected, String received, IBlock block, IContext context, IResultSet result, SgmlPage page) throws PluginException {
        try {
            if (page == null) {
                result.addResult(Failure.INSTANCE, block, new DefaultAlignmentException(expected, received));
            } else {
                result.addResult(Failure.INSTANCE, block, new DefaultAlignmentException(expected, received), SRServices.get(IWritableFactoryManager.class).get(Page.class).newWritable(page));
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
    }

    /**
     * Perform date comparisons.
     * 
     * @param compare
     *            The plugin which hold format and tolerance information.
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     * @param block
     *            The block.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param page
     *            The client page.
     * @return true, of equals considering tolerance, false, otherwise.
     * @throws PluginException
     *             On comparison errors.
     */
    public static boolean compareDate(PluginCompareDate compare, String expected, String received, IBlock block, IContext context, IResultSet result, SgmlPage page) throws PluginException {
        boolean res = true;
        DateTimeFormatter fmt = null;
        try {
            fmt = DateTimeFormat.forPattern(compare.getFormat());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            result.addResult(Failure.INSTANCE, block, new PluginException(e));
            res = false;
        }
        if (fmt != null) {
            try {
                DateTime dtExpected = fmt.parseDateTime(expected);
                DateTime dtReceived = fmt.parseDateTime(received);
                if (Math.abs(dtExpected.getMillis() - dtReceived.getMillis()) <= compare.getTolerance()) {
                    result.addResult(Success.INSTANCE, block);
                } else {
                    addError(expected, received, block, context, result, page);
                    res = false;
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                addError(expected, received, block, context, result, page);
            }
        }
        return res;
    }

    /**
     * Compare two nodes. The expected node can have less attributes than
     * received content, this is due to WebDriver API which does not provide
     * much reflection on WebElements.
     * 
     * @param compare
     *            The compare plugin.
     * @param expected
     *            The expected content.
     * @param received
     *            The received content.
     * @param block
     *            The context block.
     * @param context
     *            The context.
     * @param result
     *            The result information.
     * @return true, if can be considered equals, false, otherwise.
     */
    public static boolean compareNode(PluginCompareNode compare, Element expected, HtmlElement received, IBlock block, IContext context, IResultSet result) {
        int errors = compareTexts(compare, context, result, expected, received, 0);
        Nodes expChildren = expected.query("descendant::*");
        List<?> recChildren = received.getByXPath("descendant::*");
        List<DomNode> visible = new LinkedList<DomNode>();
        for (Object we : recChildren) {
            if (we instanceof HtmlElement) {
                if (((HtmlElement) we).isDisplayed()) {
                    visible.add((DomNode) we);
                }
            } else {
                visible.add((DomNode) we);
            }
        }
        recChildren = visible;
        int min = Math.min(expChildren.size(), recChildren.size());
        for (int i = 0; i < min; i++) {
            errors = compareElements(compare, context, result, (Element) expChildren.get(i), (DomNode) recChildren.get(i), errors);
        }
        int max = Math.max(expChildren.size(), recChildren.size());
        boolean onExpected = (max == expChildren.size());
        for (int i = min; i < max; i++) {
            errors++;
            if (onExpected) {
                result.addResult(Failure.INSTANCE, context.newBlock(expChildren.get(i), compare), new PluginException("Missing element."));
            } else {
                result.addResult(Failure.INSTANCE, context.newBlock(expected, compare), new PluginException("Extra element on screen."));
            }
        }
        if (errors == 0) {
            result.addResult(Success.INSTANCE, block);
        }
        return errors == 0;
    }

    /**
     * Compare two nodes by theirs texts.
     * 
     * @param compare
     *            The plugin.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param expected
     *            The expected content.
     * @param received
     *            The received content.
     * @param errors
     *            The errors count.
     * @return The errors count adjusted.
     */
    protected static int compareTexts(PluginCompareNode compare, IContext context, IResultSet result, Element expected, DomNode received, int errors) {
        IStringNormalizer sn = UtilString.getNormalizer();
        String strExpected = sn.normalize(expected.getValue());
        String strReceived = sn.normalize(received.asText());
        if (!strExpected.equals(strReceived)) {
            errors++;
            result.addResult(Failure.INSTANCE, context.newBlock(expected, compare), new DefaultAlignmentException(strExpected, strReceived));
        }
        return errors;
    }

    /**
     * Compare two elements and return the number of errors in comparison.
     * 
     * @param compare
     *            The compare plugin.
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param e
     *            The expected content.
     * @param we
     *            The received content.
     * @param errors
     *            The errors count.
     * @return The errors count adjusted.
     */
    protected static int compareElements(PluginCompareNode compare, IContext context, IResultSet result, Element e, DomNode we, int errors) {
        errors = compareTexts(compare, context, result, e, we, errors);
        if (!e.getLocalName().equalsIgnoreCase(we.getLocalName())) {
            errors++;
            result.addResult(Failure.INSTANCE, context.newBlock(e, compare), new PluginException("Tag names do not match (expected: '" + e.getLocalName() + "', received: '" + we.getLocalName() + "'."));
        }
        for (int j = 0; j < e.getAttributeCount(); j++) {
            Attribute att = e.getAttribute(j);
            String name = att.getLocalName();
            String attExp = e.getAttributeValue(name);
            String attRec = we.getAttributes().getNamedItem(name).getTextContent();
            if (attRec == null) {
                errors++;
                result.addResult(Failure.INSTANCE, context.newBlock(e, compare), new PluginException("Attribute '" + name + "' missing (expected: '" + name + "=" + attExp + "')."));
                continue;
            }
            String attRecNorm = compare.getNormalized(attRec);
            String attExpNorm = compare.getNormalized(attExp);
            boolean match = (compare.getContains() && attRecNorm.contains(attExpNorm)) || attExpNorm.equals(attRecNorm);
            if (!match) {
                errors++;
                result.addResult(Failure.INSTANCE, context.newBlock(e, compare), new PluginException("Attribute '" + name + "' does not match (expected: '" + attExp + "', received: '" + attRec + "')."));
            }
        }
        return errors;
    }
}
