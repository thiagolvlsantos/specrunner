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
package org.specrunner.browser.assertions;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.SpecRunnerServices;
import org.specrunner.browser.util.WritablePage;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;

import com.gargoylesoftware.htmlunit.SgmlPage;

public final class PluginCompareUtils {

    private PluginCompareUtils() {
    }

    public static boolean compare(String expected, String received, IBlock block, IContext context, IResultSet result, SgmlPage page) throws PluginException {
        boolean res = true;
        if (expected.equals(received)) {
            result.addResult(Status.SUCCESS, block);
        } else {
            addError(expected, received, block, context, result, page);
            res = false;
        }
        return res;
    }

    protected static void addError(String expected, String received, IBlock block, IContext context, IResultSet result, SgmlPage page) throws PluginException {
        try {
            IStringAligner al = SpecRunnerServices.get(IStringAlignerFactory.class).align(expected, received);
            if (page != null) {
                result.addResult(Status.FAILURE, block, new DefaultAlignmentException(al));
            } else {
                result.addResult(Status.FAILURE, block, new DefaultAlignmentException(al), new WritablePage(page));
            }
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    public static boolean compareDate(String format, long tolerance, String expected, String received, IBlock block, IContext context, IResultSet result, SgmlPage page) throws PluginException {
        boolean res = true;
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
            DateTime dtExpected = fmt.parseDateTime(expected);
            DateTime dtReceived = fmt.parseDateTime(received);
            if (Math.abs(dtExpected.getMillis() - dtReceived.getMillis()) <= tolerance) {
                result.addResult(Status.SUCCESS, block);
            } else {
                addError(expected, received, block, context, result, page);
                res = false;
            }
        } catch (Exception e) {
            addError(expected, received, block, context, result, page);
        }
        return res;
    }
}