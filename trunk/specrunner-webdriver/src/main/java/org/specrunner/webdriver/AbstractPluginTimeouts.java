/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Timeouts;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;

/**
 * Partial implementation for timeout interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginTimeouts extends AbstractPluginOptions {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, Options options) throws PluginException {
        doEnd(context, result, client, options, options.timeouts());
        result.addResult(Success.INSTANCE, context.peek());
    }

    /**
     * Perform an option timeout action.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param options
     *            The options.
     * @param timeouts
     *            The timeouts.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, Options options, Timeouts timeouts) throws PluginException;
}
