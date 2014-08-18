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
package org.specrunner.webdriver.impl;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.core.ParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IHtmlUnitDriver;
import org.specrunner.webdriver.IWait;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Wait default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class WaitDefault extends ParameterHolder implements IWait {
    /**
     * To seconds milliseconds factor.
     */
    private static final int TO_SECONDS = 1000;

    /**
     * The interval.
     */
    protected Long interval = DEFAULT_INTERVAL;

    /**
     * The max wait time.
     */
    protected Long maxwait = DEFAULT_MAXWAIT;

    /**
     * Wait for XPath condition.
     */
    protected String waitfor = DEFAULT_WAITFOR;

    @Override
    public void reset() {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("reset()");
        }
        interval = DEFAULT_INTERVAL;
        maxwait = DEFAULT_MAXWAIT;
        waitfor = DEFAULT_WAITFOR;
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_INTERVAL, this);
        fm.set(FEATURE_MAXWAIT, this);
        fm.set(FEATURE_WAITFOR, this);
        fm.set(FEATURE_WAIT, this);
    }

    @Override
    public Long getInterval() {
        return interval;
    }

    @Override
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    @Override
    public Long getMaxwait() {
        return maxwait;
    }

    @Override
    public void setMaxwait(Long maxwait) {
        this.maxwait = maxwait;
    }

    @Override
    public String getWaitfor() {
        return waitfor;
    }

    @Override
    public void setWaitfor(String waitfor) {
        this.waitfor = waitfor;
    }

    @Override
    public boolean isWaitForClient(IContext context, IResultSet result, WebDriver client) {
        return true;
    }

    @Override
    public void waitForClient(IContext context, IResultSet result, WebDriver client) throws PluginException {
        try {
            (new WebDriverWait(client, maxwait / TO_SECONDS, interval)).until(getWaitCondition(context, result, client, System.currentTimeMillis(), maxwait));
        } catch (TimeoutException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public String getWaitfor(IContext context, IResultSet result, WebDriver client) throws PluginException {
        return waitfor;
    }

    /**
     * Return the condition to wait. If <code>waitfor</code> attribute is
     * provided, the condition turn into &lt;xpath&gt;.isDisplayed().
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param start
     *            The begin time.
     * @param timeout
     *            The timeout.
     * @return The expected condition.
     * @throws PluginException
     *             On wait conditions.
     */
    public ExpectedCondition<?> getWaitCondition(IContext context, IResultSet result, WebDriver client, long start, Long timeout) throws PluginException {
        String test = getWaitfor(context, result, client);
        if (test != null) {
            return ExpectedConditions.visibilityOfElementLocated(By.xpath(test));
        }
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                if (d instanceof IHtmlUnitDriver) {
                    WebClient client = ((IHtmlUnitDriver) d).getWebClient();
                    long time = System.currentTimeMillis();
                    int count = client.waitForBackgroundJavaScript(interval);
                    while (count > 0 && (System.currentTimeMillis() - time <= interval)) {
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