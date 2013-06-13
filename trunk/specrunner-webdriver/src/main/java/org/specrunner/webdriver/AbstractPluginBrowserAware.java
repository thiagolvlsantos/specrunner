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
package org.specrunner.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.WebClient;

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
     * Wait for feature. Use it to set generic XPath wait condition.
     */
    public static final String FEATURE_WAITFOR = AbstractPluginBrowserAware.class.getName() + ".waitfor";

    /**
     * Wait for XPath condition.
     */
    private String waitfor;

    /**
     * Default timeout to finish the action.
     */
    public static final String FEATURE_TIMEOUT = AbstractPluginBrowserAware.class.getName() + ".timeout";

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
     * XPath expression to wait for.
     * 
     * @return The wait expression.
     */
    public String getWaitfor() {
        return waitfor;
    }

    /**
     * Set the XPath condition to wait for.
     * 
     * @param waitfor
     *            The condition.
     */
    public void setWaitfor(String waitfor) {
        this.waitfor = waitfor;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.set(FEATURE_INTERVAL, this);
        fh.set(FEATURE_MAXWAIT, this);
        fh.set(FEATURE_WAITFOR, this);
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
        if (isWaitForClient()) {
            waitForClient(client);
        }
        doEnd(context, result, client);
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
    protected boolean isWaitForClient() {
        return true;
    }

    /**
     * Wait for client. If wait is set it already has waited for the period time
     * set.
     * 
     * @param client
     *            The client.
     */
    protected void waitForClient(WebDriver client) {
        if (getWait() == null) {
            (new WebDriverWait(client, maxwait, interval)).until(getWaitCondition(System.currentTimeMillis(), getTimeout()));
        }
    }

    /**
     * Return the condition to wait. If <code>waitfor</code> attribute is
     * provided, the condition turn into &lt;xpath&gt;.isDisplayed().
     * 
     * @param start
     *            The begin time.
     * @param timeout
     *            The timeout.
     * @return The expected condition.
     */
    protected ExpectedCondition<?> getWaitCondition(final long start, final Long timeout) {
        if (waitfor != null) {
            return ExpectedConditions.visibilityOfElementLocated(By.xpath(waitfor));
        }
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                if (d instanceof IHtmlUnitDriver) {
                    WebClient client = ((IHtmlUnitDriver) d).getWebClient();
                    long time = System.currentTimeMillis();
                    int count = client.waitForBackgroundJavaScript(interval);
                    while (count > 0 && (System.currentTimeMillis() - time <= maxwait)) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info(count + " threads, waiting for " + interval + "mls on max of " + maxwait + "mls.");
                        }
                        count = client.waitForBackgroundJavaScript(interval);
                    }
                    return true;
                }
                return true;
            }
        };
    }
}