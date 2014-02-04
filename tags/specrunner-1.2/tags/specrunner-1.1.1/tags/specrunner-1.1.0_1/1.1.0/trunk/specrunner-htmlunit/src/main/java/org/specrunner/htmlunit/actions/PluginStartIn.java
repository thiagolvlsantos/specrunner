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
package org.specrunner.htmlunit.actions;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.string.IStringProvider;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Sets initial page of a given driver. Once set, relative references can be
 * made.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginStartIn extends AbstractPluginUrlAware {

    /**
     * Start reference for a given browser.
     */
    public static final String START_IN = "startIn";

    /**
     * String provider.
     */
    private String provider;

    /**
     * The <code>IStringProvider</code> which can give information about the
     * start url.
     * 
     * @return The provider class name.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * The provide class name.
     * 
     * @param provider
     *            The provider.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client) throws PluginException {
        Node node = context.getNode();
        String tmp = getBrowserName();
        String u = null;
        if (provider == null) {
            u = getUrl() != null ? getUrl() : node.getValue();
            u = String.valueOf(UtilEvaluator.evaluate(u, context));
        } else {
            try {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Trying to recover base URL from provider '" + provider + "'.");
                }
                u = getBaseFromProvider(context);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Base url provided is '" + u + "'.");
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
                result.addResult(Failure.INSTANCE, context.newBlock(node, this), e);
                return;
            }
        }
        context.saveGlobal(getBaseForBrowser(tmp), u);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Browser named '" + tmp + "' base url set to '" + u + "'.");
        }
        result.addResult(Success.INSTANCE, context.newBlock(node, this));
    }

    /**
     * Get the URL from a provider.
     * 
     * @param context
     *            The context.
     * @return The provider url.
     * @throws Exception
     *             On provider action errors.
     */
    protected String getBaseFromProvider(IContext context) throws Exception {
        return ((IStringProvider) Class.forName(provider).newInstance()).newString(context);
    }

    /**
     * Gets the start URL for a given browser name.
     * 
     * @param browserName
     *            The browser name.
     * @return The browser start in name to be used in context lookups.
     */
    public static String getBaseForBrowser(String browserName) {
        return browserName + "_" + START_IN;
    }
}