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
package org.specrunner.webdriver;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.parameters.IParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

/**
 * Something that waits for actions.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWait extends IParameterHolder {

    /**
     * Feature for interval.
     */
    String FEATURE_INTERVAL = IWait.class.getName() + ".interval";
    /**
     * Default interval.
     */
    Long DEFAULT_INTERVAL = 100L;

    /**
     * Feature to set max interval.
     */
    String FEATURE_MAXWAIT = IWait.class.getName() + ".maxwait";
    /**
     * Default max wait.
     */
    Long DEFAULT_MAXWAIT = 1000L;

    /**
     * Wait for feature. Use it to set generic XPath wait condition.
     */
    String FEATURE_WAITFOR = IWait.class.getName() + ".waitfor";
    /**
     * Default wait for is <code>null</code>.
     */
    String DEFAULT_WAITFOR = null;

    /**
     * Wait for feature. Use it to set generic XPath wait condition.
     */
    String FEATURE_WAITFOR_SEPARATOR = IWait.class.getName() + ".waitforSeparator";
    /**
     * Default wait for separator is <code>;</code>.
     */
    String DEFAULT_WAITFOR_SEPARATOR = ";";

    /**
     * Wait for feature. Use it to set generic XPath wait method separator.
     */
    String FEATURE_WAITFOR_METHOD_SEPARATOR = IWait.class.getName() + ".waitforMethodSeparator";
    /**
     * Default wait for separator is <code>:</code>.
     */
    String DEFAULT_WAITFOR_METHOD_SEPARATOR = ":";

    /**
     * Wait for prefix.
     */
    String FEATURE_WAITFOR_PREFIX = IWait.class.getName() + ".waitforPrefix";
    /**
     * Default wait for prefix is <code>null</code>.
     */
    String DEFAULT_WAITFOR_PREFIX = null;

    /**
     * Wait for suffix.
     */
    String FEATURE_WAITFOR_SUFFIX = IWait.class.getName() + ".waitforSuffix";
    /**
     * Default wait for suffix is <code>null</code>.
     */
    String DEFAULT_WAITFOR_SUFFIX = null;

    /**
     * Clear any previous settings.
     */
    void reset();

    /**
     * The interval between JavaScript finish checks. Default is '100'
     * milliseconds.
     * 
     * @return The interval.
     */
    Long getInterval();

    /**
     * Change the interval.
     * 
     * @param interval
     *            The interval.
     */
    void setInterval(Long interval);

    /**
     * The max time to wait for JavaScript return. Default is '1000'
     * milliseconds.
     * 
     * @return The max time to wait for JavaScript.
     */
    Long getMaxwait();

    /**
     * Set the max wait interval.
     * 
     * @param maxwait
     *            The max wait.
     */
    void setMaxwait(Long maxwait);

    /**
     * XPath expression to wait for.
     * 
     * @return The wait expression.
     */
    String getWaitfor();

    /**
     * Set the XPath condition to wait for.
     * 
     * @param waitfor
     *            The condition.
     */
    void setWaitfor(String waitfor);

    /**
     * XPath expression to wait for separator.
     * 
     * @return The wait separator.
     */
    String getWaitforSeparator();

    /**
     * Set the XPath separator.
     * 
     * @param waitforSeparator
     *            The separator.
     */
    void setWaitforSeparator(String waitforSeparator);

    /**
     * XPath expression to wait for method separator. When separator is not
     * specified <code>visibilityOfElementLocated</code> will be used. Method
     * options are those of <code>ExpectedConditions</code> which receives a
     * single <code>By</code> parameter.
     * 
     * @return The wait method separator.
     */
    String getWaitforMethodSeparator();

    /**
     * Set the XPath separator.
     * 
     * @param waitforSeparator
     *            The separator.
     */
    void setWaitforMethodSeparator(String waitforSeparator);

    /**
     * XPath expression to wait for.
     * 
     * @return The wait expression prefix.
     */
    String getWaitforPrefix();

    /**
     * Set the XPath condition to wait for.
     * 
     * @param waitforPrefix
     *            The condition.
     */
    void setWaitforPrefix(String waitforPrefix);

    /**
     * XPath expression to wait for.
     * 
     * @return The wait expression.
     */
    String getWaitforSuffix();

    /**
     * Set the XPath condition to wait for.
     * 
     * @param waitforSuffix
     *            The condition.
     */
    void setWaitforSuffix(String waitforSuffix);

    /**
     * Sign actions to wait for browser response.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * 
     * @return true, when wait is desired, false, otherwise. Default is true.
     */
    boolean isWaitForClient(IContext context, IResultSet result, WebDriver client);

    /**
     * Wait for client. If wait is set it already has waited for the period time
     * set.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * 
     * @param client
     *            The client.
     * @throws PluginException
     *             On wait for client errors.
     */
    void waitForClient(IContext context, IResultSet result, WebDriver client) throws PluginException;

    /**
     * Get the wait for condition.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @return The wait for condition, if exists, null otherwise.
     * @throws PluginException
     *             On plugin XPath recover errors.
     */
    String getWaitfor(IContext context, IResultSet result, WebDriver client) throws PluginException;
}
