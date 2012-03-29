/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.WebDriver;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Comparation utilities.
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
            result.addResult(Status.SUCCESS, block);
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
                result.addResult(Status.FAILURE, block, new DefaultAlignmentException(al), new WritablePage(client));
            } else {
                result.addResult(Status.FAILURE, block, new DefaultAlignmentException(al));
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
    }

    public static boolean compareDate(String format, long tolerance, String expected, String received, IBlock block, IContext context, IResultSet result, WebDriver client) throws PluginException {
        boolean res = true;
        DateTimeFormatter fmt = null;
        try {
            fmt = DateTimeFormat.forPattern(format);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            result.addResult(Status.FAILURE, block, new PluginException(e));
            res = false;
        }
        if (fmt != null) {
            try {
                DateTime dtExpected = fmt.parseDateTime(expected);
                DateTime dtReceived = fmt.parseDateTime(received);
                if (Math.abs(dtExpected.getMillis() - dtReceived.getMillis()) <= tolerance) {
                    result.addResult(Status.SUCCESS, block);
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
}