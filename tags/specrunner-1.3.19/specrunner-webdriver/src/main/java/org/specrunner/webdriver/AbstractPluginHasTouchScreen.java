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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.TouchScreen;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;

/**
 * Partial implementation for touch screen interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginHasTouchScreen extends AbstractPluginBrowserAware {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        if (client instanceof HasTouchScreen) {
            doEnd(context, result, client, ((HasTouchScreen) client).getTouch());
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("The WebDriver '" + client.getClass().getName() + "' is not a instance of HasTouchScreen."));
        }
    }

    /**
     * Perform actions on touch screen devices.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param touch
     *            The touch screen.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, TouchScreen touch) throws PluginException;
}