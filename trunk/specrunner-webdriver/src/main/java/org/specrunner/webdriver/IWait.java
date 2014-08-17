/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
     * Wait time feature.
     */
    String FEATURE_WAIT = IWait.class.getName() + ".wait";

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