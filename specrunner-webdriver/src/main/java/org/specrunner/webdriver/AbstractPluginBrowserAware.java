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
package org.specrunner.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilString;

/**
 * A generic plugin that acts over a webdriver.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginBrowserAware extends AbstractPluginValue {

    /**
     * Feature for interval.
     */
    public static final String FEATURE_INTERVAL = AbstractPluginBrowserAware.class.getName() + ".interval";
    /**
     * Default interval.
     */
    private static final Long DEFAULT_INTERVAL = 100L;
    /**
     * The interval.
     */
    private Long interval = DEFAULT_INTERVAL;

    /**
     * Feature to set max interval.
     */
    public static final String FEATURE_MAXWAIT = AbstractPluginBrowserAware.class.getName() + ".maxwait";
    /**
     * Default max wait.
     */
    private static final Long DEFAULT_MAXWAIT = 1000L;
    /**
     * The max wait time.
     */
    private Long maxwait = DEFAULT_MAXWAIT;

    /**
     * Feature to set normalized state.
     */
    public static final String FEATURE_NORMALIZED = AbstractPluginBrowserAware.class.getName() + ".normalized";
    /**
     * The normalized version.
     */
    private Boolean normalized = Boolean.TRUE;

    /**
     * Default timeout to finish the action.
     */
    public static final String FEATURE_TIMEOUT = AbstractPluginBrowserAware.class.getName() + ".timeout";

    /**
     * The max time to wait for JavaScript return. Default is '1000'
     * milliseconds.
     * 
     * @return The max time to wait for JavaScript.
     */
    public Long getMaxwait() {
        return maxwait;
    }

    /**
     * Set the max wait interval.
     * 
     * @param maxwait
     *            The max wait.
     */
    public void setMaxwait(Long maxwait) {
        this.maxwait = maxwait;
    }

    /**
     * The interval between JavaScript finish checks. Default is '100'
     * milliseconds.
     * 
     * @return The interval.
     */
    public Long getInterval() {
        return interval;
    }

    /**
     * Change the interval.
     * 
     * @param interval
     *            The interval.
     */
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    /**
     * If normalized is false, the expected and received String are not compared
     * using their normalized version (trim+remove extra spaces).
     * 
     * @return If normalized is enable or not.
     */
    public Boolean getNormalized() {
        return normalized;
    }

    /**
     * Set the normalized state.
     * 
     * @param normalized
     *            true, to normalize, false, otherwise.
     */
    public void setNormalized(Boolean normalized) {
        this.normalized = normalized;
    }

    /**
     * Get the normalized version of a string.
     * 
     * @param str
     *            The string to be normalized.
     * @return The normalized version of the string, if normalized=true.
     */
    public String getNormalized(String str) {
        if (getNormalized()) {
            return UtilString.normalize(str);
        }
        return str;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.set(FEATURE_INTERVAL, this);
        fh.set(FEATURE_MAXWAIT, this);
        fh.set(FEATURE_NORMALIZED, this);
        fh.set(FEATURE_TIMEOUT, this);
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        String tmp = getBrowserName();
        WebDriver client = (WebDriver) context.getByName(tmp);
        if (client == null) {
            result.addResult(Failure.INSTANCE, context.peek(), "Browser instance named '" + tmp + "' not created. See PluginBrowser.");
            return;
        }
        doEnd(context, result, client);
        if (isWait()) {
            waitForClient(client);
        }
    }

    /**
     * Gets the browser name.
     * 
     * @return The name.
     */
    public String getBrowserName() {
        return getName() != null ? getName() : PluginBrowser.BROWSER_NAME;
    }

    /**
     * Method delegation which receives the browser to be used by subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param client
     *            The browser.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException;

    /**
     * Sign actions to wait for browser response.
     * 
     * @return true, when wait is desired, false, otherwise. Default is true.
     */
    protected boolean isWait() {
        return true;
    }

    /**
     * Wait for client. If sleep is set, wait for sleep time.
     * 
     * @param client
     *            The client.
     */
    protected void waitForClient(WebDriver client) {
        if (getSleep() == null) {
            (new WebDriverWait(client, maxwait, interval)).until(getCondition(System.currentTimeMillis(), getTimeout()));
        }
    }

    /**
     * Return the condition to wait.
     * 
     * @param start
     *            The begin time.
     * @param timeout
     *            The timeout.
     * @return The expected condition.
     */
    protected ExpectedCondition<Boolean> getCondition(final long start, final Long timeout) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                // WebElement e = d.findElement(By.tagName("body"));
                // if (UtilLog.LOG.isInfoEnabled()) {
                // UtilLog.LOG.info("Waiting for body or timeout (" +
                // (System.currentTimeMillis() - start) + "<" + timeout + ").");
                // }
                // return (timeout != null && (System.currentTimeMillis() -
                // start) > timeout) || e.isDisplayed();
                return true;
            }
        };
    }
}