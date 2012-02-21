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
package org.specrunner.browser;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * A generic plugin that acts over a browser.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginBrowserAware extends AbstractPluginValue {

    /**
     * Default interval feature.
     */
    public static final String FEATURE_INTERVAL = AbstractPluginBrowserAware.class.getName() + ".interval";
    private static final Long DEFAULT_INTERVAL = 100L;
    private Long interval = DEFAULT_INTERVAL;

    /**
     * Default max time of feature.
     */
    public static final String FEATURE_MAXWAIT = AbstractPluginBrowserAware.class.getName() + ".maxwait";
    private static final Long DEFAULT_MAXWAIT = 1000L;
    private Long maxwait = DEFAULT_MAXWAIT;

    /**
     * Default timeout to finish browser based actions.
     */
    public static final String FEATURE_TIMEOUT = AbstractPluginBrowserAware.class.getName() + ".timeout";

    public AbstractPluginBrowserAware() {
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

    public void setInterval(Long interval) {
        this.interval = interval;
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

    public String getBrowserName() {
        return getName() != null ? getName() : PluginBrowser.BROWSER_NAME;
    }

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