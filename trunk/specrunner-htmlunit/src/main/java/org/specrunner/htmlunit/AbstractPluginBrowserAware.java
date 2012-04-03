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
package org.specrunner.htmlunit;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * A generic plugin that acts over a browser.
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
     * Default timeout to finish browser based actions.
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
     * @return The normalized version of a string.
     */
    public Boolean getNormalized() {
        return normalized;
    }

    /**
     * Sets normalized status.
     * 
     * @param normalized
     *            The normalized option.
     */
    public void setNormalized(Boolean normalized) {
        this.normalized = normalized;
    }

    /**
     * Gets the normalized version of a string.
     * 
     * @param str
     *            The string to be normalized.
     * @return The normalized version, if normalized=true.
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
        try {
            fh.set(FEATURE_INTERVAL, "interval", Long.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fh.set(FEATURE_MAXWAIT, "maxwait", Long.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fh.set(FEATURE_NORMALIZED, "normalized", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fh.set(FEATURE_TIMEOUT, "timeout", Long.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        String tmp = getBrowserName();
        WebClient client = (WebClient) context.getByName(tmp);
        if (client == null) {
            result.addResult(Status.FAILURE, context.peek(), "Browser instance named '" + tmp + "' not created. See PluginBrowser.");
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
     * Sign actions to wait for browser response.
     * 
     * @return true, when wait is desired, false, otherwise. Default is true.
     */
    protected boolean isWait() {
        return true;
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
    protected abstract void doEnd(IContext context, IResultSet result, WebClient client) throws PluginException;

    /**
     * Wait for client. If sleep is set, wait for sleep time.
     * 
     * @param client
     *            The client.
     */
    protected void waitForClient(WebClient client) {
        if (getSleep() == null) {
            long time = System.currentTimeMillis();
            int count = client.waitForBackgroundJavaScript(interval);
            while (count > 0 && (System.currentTimeMillis() - time <= maxwait)) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(count + " threads, waiting for " + interval + "mls on max of " + maxwait + "mls.");
                }
                count = client.waitForBackgroundJavaScript(interval);
            }
        }
    }
}