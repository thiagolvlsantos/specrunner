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

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

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
    protected static final int TO_SECONDS = 1000;

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

    /**
     * Wait for XPath separator.
     */
    protected String waitforSeparator = DEFAULT_WAITFOR_SEPARATOR;

    /**
     * Wait for waitfor method token.
     */
    protected String waitforMethodSeparator = DEFAULT_WAITFOR_METHOD_SEPARATOR;

    /**
     * Wait for prefix.
     */
    protected String waitforPrefix = DEFAULT_WAITFOR_PREFIX;

    /**
     * Wait for suffix.
     */
    protected String waitforSuffix = DEFAULT_WAITFOR_SUFFIX;

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
        fm.set(FEATURE_WAITFOR_SEPARATOR, this);
        fm.set(FEATURE_WAITFOR_METHOD_SEPARATOR, this);
        fm.set(FEATURE_WAITFOR_PREFIX, this);
        fm.set(FEATURE_WAITFOR_SUFFIX, this);
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
    public String getWaitforSeparator() {
        return waitforSeparator;
    }

    @Override
    public void setWaitforSeparator(String waitforSeparator) {
        this.waitforSeparator = waitforSeparator;
    }

    @Override
    public String getWaitforMethodSeparator() {
        return waitforMethodSeparator;
    }

    @Override
    public void setWaitforMethodSeparator(String waitforMethodSeparator) {
        this.waitforMethodSeparator = waitforMethodSeparator;
    }

    @Override
    public String getWaitforPrefix() {
        return waitforPrefix;
    }

    @Override
    public void setWaitforPrefix(String waitforPrefix) {
        this.waitforPrefix = waitforPrefix;
    }

    @Override
    public String getWaitforSuffix() {
        return waitforSuffix;
    }

    @Override
    public void setWaitforSuffix(String waitforSuffix) {
        this.waitforSuffix = waitforSuffix;
    }

    @Override
    public boolean isWaitForClient(IContext context, IResultSet result, WebDriver client) {
        return true;
    }

    @Override
    public void waitForClient(IContext context, IResultSet result, WebDriver client) throws PluginException {
        try {
            List<ExpectedCondition<?>> conditions = getWaitCondition(context, result, client);
            for (int i = 0; i < conditions.size(); i++) {
                ExpectedCondition<?> c = conditions.get(i);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Wait condition[" + (i + 1) + " of " + conditions.size() + "]: " + c);
                }
                (new WebDriverWait(client, maxwait / TO_SECONDS, interval)).until(c);
            }
        } catch (TimeoutException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public String getWaitfor(IContext context, IResultSet result, WebDriver client) throws PluginException {
        StringBuilder sb = new StringBuilder();
        if (waitfor != null) {
            if (waitforPrefix != null) {
                sb.append(waitforPrefix);
                sb.append(waitforSeparator);
            }
            sb.append(waitfor);
            if (waitforSuffix != null) {
                sb.append(waitforSeparator);
                sb.append(waitforSuffix);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
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
     * @return The expected conditions.
     * @throws PluginException
     *             On wait conditions.
     */
    public List<ExpectedCondition<?>> getWaitCondition(IContext context, IResultSet result, WebDriver client) throws PluginException {
        List<ExpectedCondition<?>> conditions = new LinkedList<ExpectedCondition<?>>();
        String test = getWaitfor(context, result, client);
        if (test != null) {
            StringTokenizer st = new StringTokenizer(test, waitforSeparator);
            while (st.hasMoreTokens()) {
                conditions.add(getTestWait(st.nextToken()));
            }
        } else {
            conditions.add(getDefaultWait());
        }
        return conditions;
    }

    /**
     * Get test wait conditions.
     * 
     * @param test
     *            Wait expression.
     * @return Conditions.
     * @throws PluginException
     *             On load errors.
     */
    protected ExpectedCondition<?> getTestWait(String test) throws PluginException {
        int pos = test.indexOf(getWaitforMethodSeparator());
        if (pos > 0) {
            String method = test.substring(0, pos);
            String xpath = test.substring(pos + getWaitforMethodSeparator().length());
            try {
                Method m = ExpectedConditions.class.getMethod(method, By.class);
                if (m == null) {
                    throw new PluginException("Method '" + method + "' not found.");
                }
                return (ExpectedCondition<?>) m.invoke(null, By.xpath(xpath));
            } catch (Exception e) {
                throw new PluginException(e);
            }
        }
        return ExpectedConditions.visibilityOfElementLocated(By.xpath(test));
    }

    /**
     * Default wait strategy.
     * 
     * @return Condition.
     */
    protected ExpectedCondition<Boolean> getDefaultWait() {
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

            @Override
            public String toString() {
                return "Wait default.";
            }
        };
    }
}