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
package org.specrunner.webdriver.actions;

import java.net.MalformedURLException;
import java.net.URL;

import nu.xom.Node;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IAction;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.AbstractPluginUrlAware;

/**
 * Open a given URL.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginOpen extends AbstractPluginUrlAware implements IAction {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        Node node = context.getNode();
        String u = getUrl() != null ? getUrl() : node.getValue();
        String tmp = getBrowserName();
        if (!u.startsWith("http:") && !u.startsWith("file:")) {
            String baseUrl = (String) context.getByName(PluginStartIn.getBaseForBrowser(tmp));
            if (baseUrl == null) {
                throw new PluginException("Could not find base url for browser '" + tmp + "'.");
            }
            String target;
            try {
                URL base = new URL(baseUrl);
                target = new URL(base, u).toString();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Relative url resolved from '" + u + "' to '" + target + "'.");
                }
            } catch (MalformedURLException e) {
                throw new PluginException("Could not resolve '" + baseUrl + "' with " + u + ".");
            }
            u = target;
        }
        PluginException error = null;
        try {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Openning '" + u + "' on browser named '" + tmp + "'.");
            }
            client.get(u);
        } catch (Exception e) {
            error = new PluginException(e);
        }
        if (error != null && !isIgnorable(error)) {
            throw error;
        } else {
            result.addResult(Status.SUCCESS, context.newBlock(node, this));
        }
    }

    /**
     * Should return true if the error is ignorable.
     * 
     * @param error
     *            The error.
     * @return true, if ignorable, false, otherwise. Default is 'false'.
     */
    protected boolean isIgnorable(PluginException error) {
        return false;
    }
}