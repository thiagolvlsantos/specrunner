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
package org.specrunner.webdriver.assertions;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.webdriver.util.WritablePage;

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
     * @param client
     *            The web driver.
     * @return true, if equals, false, otherwise.
     * @throws PluginException
     *             On comparison errors.
     */
    public static boolean compare(String expected, String received, IBlock block, IContext context, IResultSet result, WebDriver client) throws PluginException {
        boolean res = true;
        if (expected.equals(received)) {
            result.addResult(Success.INSTANCE, block);
        } else {
            addError(expected, received, block, context, result, client);
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
     * @param client
     *            The web driver.
     * @throws PluginException
     *             On errors.
     */
    protected static void addError(String expected, String received, IBlock block, IContext context, IResultSet result, WebDriver client) throws PluginException {
        try {
            IStringAligner al = SpecRunnerServices.get(IStringAlignerFactory.class).align(expected, received);
            if (client != null) {
                result.addResult(Failure.INSTANCE, block, new DefaultAlignmentException(al), new WritablePage(client));
            } else {
                result.addResult(Failure.INSTANCE, block, new DefaultAlignmentException(al));
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
     * @param client
     *            The web driver.
     * @return true, of equals considering tolerance, false, otherwise.
     * @throws PluginException
     *             On comparison errors.
     */
    public static boolean compareDate(PluginCompareDate compare, String expected, String received, IBlock block, IContext context, IResultSet result, WebDriver client) throws PluginException {
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
                    addError(expected, received, block, context, result, client);
                    res = false;
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                addError(expected, received, block, context, result, client);
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
     * @param client
     *            The WebDriver client.
     * @return true, if can be considered equals, false, otherwise.
     */
    public static boolean compareNode(PluginCompareNode compare, Element expected, WebElement received, IBlock block, IContext context, IResultSet result, WebDriver client) {
        int errors = compareTexts(compare, context, result, expected, received, 0);
        Nodes expChildren = expected.query("descendant::*");
        List<WebElement> recChildren = received.findElements(By.xpath("descendant::*"));
        List<WebElement> visible = new LinkedList<WebElement>();
        for (WebElement we : recChildren) {
            if (we.isDisplayed()) {
                visible.add(we);
            }
        }
        recChildren = visible;
        int min = Math.min(expChildren.size(), recChildren.size());
        for (int i = 0; i < min; i++) {
            errors = compareElements(compare, context, result, (Element) expChildren.get(i), recChildren.get(i), errors);
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
    protected static int compareTexts(PluginCompareNode compare, IContext context, IResultSet result, Element expected, WebElement received, int errors) {
        String strExpected = UtilString.normalize(expected.getValue());
        String strReceived = UtilString.normalize(received.getText());
        if (!strExpected.equals(strReceived)) {
            errors++;
            IStringAligner sa = SpecRunnerServices.get(IStringAlignerFactory.class).align(strExpected, strReceived);
            result.addResult(Failure.INSTANCE, context.newBlock(expected, compare), new DefaultAlignmentException(sa));
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
    protected static int compareElements(PluginCompareNode compare, IContext context, IResultSet result, Element e, WebElement we, int errors) {
        errors = compareTexts(compare, context, result, e, we, errors);
        if (!e.getLocalName().equalsIgnoreCase(we.getTagName())) {
            errors++;
            result.addResult(Failure.INSTANCE, context.newBlock(e, compare), new PluginException("Tag names do not match (expected: '" + e.getLocalName() + "', received: '" + we.getTagName() + "'."));
        }
        for (int j = 0; j < e.getAttributeCount(); j++) {
            Attribute att = e.getAttribute(j);
            String name = att.getLocalName();
            String attExp = e.getAttributeValue(name);
            String attRec = we.getAttribute(name);
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