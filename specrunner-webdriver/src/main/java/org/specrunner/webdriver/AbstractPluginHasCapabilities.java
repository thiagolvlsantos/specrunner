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

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;

/**
 * Partial implementation for capabilities interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginHasCapabilities extends AbstractPluginBrowserAware {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        if (client instanceof HasCapabilities) {
            doEnd(context, result, client, (HasCapabilities) client);
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("The WebDriver '" + client.getClass().getName() + "' is not a instance of HasCapabilities."));
        }
    }

    /**
     * Perform an action on <code>HasCapabilities</code> devices.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param capabilities
     *            The capabilities.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, HasCapabilities capabilities) throws PluginException;
}