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

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFind;
import org.specrunner.htmlunit.IFinder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Check if an id, name, value, xpath, etc is present.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginPresent extends AbstractPluginFind implements IAssertion {

    /**
     * Exact number of elements selected.
     */
    private Integer count;
    /**
     * Minimum number of elements selected.
     */
    private Integer min;
    /**
     * Maximum number of elements selected.
     */
    private Integer max;

    /**
     * Gets the exact count match.
     * 
     * @return The count.
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets the exact count match.
     * 
     * @param count
     *            The count.
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Gets the minimum count.
     * 
     * @return The minimum.
     */
    public Integer getMin() {
        return min;
    }

    /**
     * Sets the minimum.
     * 
     * @param min
     *            The minimum.
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * Set maximum counter.
     * 
     * @return The maximum.
     */
    public Integer getMax() {
        return max;
    }

    /**
     * Sets the maximum.
     * 
     * @param max
     *            The maximum.
     */
    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client, SgmlPage page) throws PluginException {
        IFinder finder = getFinderInstance(context);
        List<?> list = finder.find(context, result, client, page);
        int failure = 0;
        if (getCount() != null) {
            if (list.size() != getCount()) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("The expected count of elements was '" + count + "', but '" + list.size() + "' was received."));
                failure++;
            }
        } else {
            if (list.isEmpty()) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element not found."));
                failure++;
            }
        }
        if (getMin() != null) {
            if (list.size() < getMin()) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("The expected minimum of elements was '" + getMin() + "', but '" + list.size() + "' was received."));
                failure++;
            }
        }
        if (getMax() != null) {
            if (list.size() > getMax()) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("The expected maximum of elements was '" + getMax() + "', but '" + list.size() + "' was received."));
                failure++;
            }
        }
        if (failure == 0) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        // useless.
    }
}