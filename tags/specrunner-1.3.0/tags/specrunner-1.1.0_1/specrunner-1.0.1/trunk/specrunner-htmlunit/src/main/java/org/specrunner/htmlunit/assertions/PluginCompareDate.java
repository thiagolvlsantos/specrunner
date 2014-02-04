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
package org.specrunner.htmlunit.assertions;

import nu.xom.Element;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.util.impl.CellAdapter;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class PluginCompareDate extends PluginCompare {

    public static final String FEATURE_FORMAT = PluginCompareDate.class.getName() + ".format";
    /** Date format, default is null. */
    private String format;

    public static final String FEATURE_TOLERANCE = PluginCompareDate.class.getName() + ".tolerance";
    /** Date tolerance, default 1s */
    private static final Long DEFAULT_TOLERANCE = 1000L;
    private Long tolerance = DEFAULT_TOLERANCE;

    public PluginCompareDate() {
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getTolerance() {
        return tolerance;
    }

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
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        if (format == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Date comparation missing 'format' attribute."));
            return;
        }
        Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String expected = String.valueOf(tmp);
        String received = element.asText();
        PluginCompareUtils.compareDate(format, tolerance, expected, received, context.newBlock(context.getNode(), this), context, result, page);
    }

    public static boolean isDate(Element element) {
        CellAdapter ca = new CellAdapter(element);
        return ca.hasAttribute("type") && ca.getAttribute("type").equals("date");
    }
}