/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

/**
 * Partial implementation for mouse interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginMouse extends AbstractPluginHasInputDevices {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, HasInputDevices input) throws PluginException {
        doEnd(context, result, client, input, input.getMouse());
    }

    /**
     * Perform actions on a mouse enabled device.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param input
     *            The device.
     * @param mouse
     *            The mouse action.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, HasInputDevices input, Mouse mouse) throws PluginException;
}
