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

import nu.xom.Element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.util.impl.CellAdapter;

/**
 * Compare date fields.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareDate extends PluginCompare {

    /**
     * Feature to set data format on comparisons.
     */
    public static final String FEATURE_FORMAT = PluginCompareDate.class.getName() + ".format";
    /** Date format, default is null. */
    private String format;

    /**
     * Feature to set tolerance on time comparation.
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
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fh.set(FEATURE_FORMAT, "format", String.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fh.set(FEATURE_TOLERANCE, "tolerance", Long.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (getFormat() == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Date comparation missing 'format' attribute."));
            return;
        }
        Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String expected = String.valueOf(tmp);
        String received = element.getText();
        PluginCompareUtils.compareDate(this, expected, received, context.newBlock(context.getNode(), this), context, result, client);
    }

    /**
     * Check if the element stand for a date.
     * 
     * @param element
     *            The element.
     * @return true, if is date comparation, false, otherwise.
     */
    public static boolean isDate(Element element) {
        CellAdapter ca = new CellAdapter(element);
        return ca.hasAttribute("type") && ca.getAttribute("type").equalsIgnoreCase("date");
    }
}