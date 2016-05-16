/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Compare date fields.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareDate extends PluginCompareText {

    /**
     * Feature to set data format on comparisons.
     */
    public static final String FEATURE_FORMAT = PluginCompareDate.class.getName() + ".format";
    /** Date format, default is null. */
    private String format;

    /**
     * Feature to set tolerance on time comparison.
     */
    public static final String FEATURE_TOLERANCE = PluginCompareDate.class.getName() + ".tolerance";
    /** Date tolerance, default 1s. */
    private static final Long DEFAULT_TOLERANCE = 1000L;
    /**
     * The time tolerance.
     */
    private Long tolerance = DEFAULT_TOLERANCE;

    /**
     * The date format.
     * 
     * @return The date format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the date format.
     * 
     * @param format
     *            The format.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Gets the time tolerance.
     * 
     * @return The time tolerance.
     */
    public long getTolerance() {
        return tolerance;
    }

    /**
     * Sets the time tolerance.
     * 
     * @param tolerance
     *            The time tolerance.
     */
    public void setTolerance(long tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (format == null) {
            fm.set(FEATURE_FORMAT, this);
        }
        if (tolerance == null) {
            fm.set(FEATURE_TOLERANCE, this);
        }
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        String expected = getExpected(context);
        if (format == null) {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Date comparison missing 'format' attribute."));
            return;
        }
        String received = element.asText();
        PluginCompareUtils.compareDate(this, expected, received, context.newBlock(context.getNode(), this), context, result, page);
    }

    /**
     * Get expected value for comparison.
     * 
     * @param context
     *            A context.
     * @return The string for expected value.
     * @throws PluginException
     *             On plugin error.
     */
    protected String getExpected(IContext context) throws PluginException {
        INodeHolder nh = SRServices.get(INodeHolderFactory.class).newHolder(context.getNode());
        if (getFormat() == null) {
            setFormat(nh.getAttribute(INodeHolder.ATTRIBUTE_ARGUMENT_FORMATTER_PREFIX + 0));
        }
        Object tmp = nh.getObject(context, true);
        String expected = null;
        if (tmp instanceof Date || tmp instanceof ReadableInstant || tmp instanceof ReadablePartial) {
            DateTime time = new DateTime(tmp);
            expected = time.toString(getFormat());
        } else {
            expected = String.valueOf(tmp);
        }
        return expected;
    }

    /**
     * Check if the element stand for a date.
     * 
     * @param ca
     *            The element.
     * @return true, if is date comparison, false, otherwise.
     */
    public static boolean isDate(INodeHolder ca) {
        return ca.attributeEquals("type", "date");
    }
}
