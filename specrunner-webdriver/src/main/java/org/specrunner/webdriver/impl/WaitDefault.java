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
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.AbstractPluginBrowserAware;
import org.specrunner.webdriver.IHtmlUnitDriver;
import org.specrunner.webdriver.IWait;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Wait default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class WaitDefault implements IWait {
    /**
     * To seconds milliseconds factor.
     */
    private static final int TO_SECONDS = 1000;

    @Override
    public boolean isWaitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) {
        return true;
    }

    @Override
    public void waitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) throws PluginException {
        if (plugin.getWait() == null) {
            try {
                (new WebDriverWait(client, plugin.getMaxwait() / TO_SECONDS, plugin.getInterval())).until(getWaitCondition(plugin, context, result, client, System.currentTimeMillis(), plugin.getTimeout()));
            } catch (TimeoutException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public String getWaitfor(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) throws PluginException {
        return plugin.getWaitfor();
    }

    /**
     * Return the condition to wait. If <code>waitfor</code> attribute is
     * provided, the condition turn into &lt;xpath&gt;.isDisplayed().
     * 
     * @param plugin
     *            Source plugin.
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
    public ExpectedCondition<?> getWaitCondition(final AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client, long start, Long timeout) throws PluginException {
        if (getWaitfor(plugin, context, result, client) != null) {
            return ExpectedConditions.visibilityOfElementLocated(By.xpath(getWaitfor(plugin, context, result, client)));
        }
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                if (d instanceof IHtmlUnitDriver) {
                    WebClient client = ((IHtmlUnitDriver) d).getWebClient();
                    long time = System.currentTimeMillis();
                    int count = client.waitForBackgroundJavaScript(plugin.getInterval());
                    while (count > 0 && (System.currentTimeMillis() - time <= plugin.getInterval())) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info(count + " threads, waiting for " + plugin.getInterval() + "mls on max of " + plugin.getMaxwait() + "mls.");
                        }
                        count = client.waitForBackgroundJavaScript(plugin.getInterval());
                    }
                    return true;
                }
                return true;
            }
        };
    }
}