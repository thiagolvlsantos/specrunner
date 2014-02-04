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
package org.specrunner.htmlunit.assertions;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;

import com.gargoylesoftware.htmlunit.SgmlPage;

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
            IStringAligner al = SpecRunnerServices.get(IStringAlignerFactory.class).align(expected, received);
            if (page == null) {
                result.addResult(Failure.INSTANCE, block, new DefaultAlignmentException(al));
            } else {
                result.addResult(Failure.INSTANCE, block, new DefaultAlignmentException(al), new WritablePage(page));
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
}