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

import java.util.Arrays;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

/**
 * Partial implementation for keyboard interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginKeyboard extends AbstractPluginHasInputDevices {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, HasInputDevices input) throws PluginException {
        doEnd(context, result, client, input, input.getKeyboard());
    }

    /**
     * Perform an action on input devices with keyboard.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param input
     *            The device.
     * @param keyboard
     *            The keyboard.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, HasInputDevices input, Keyboard keyboard) throws PluginException;

    /**
     * Gets the keys by the name set in name attribute.
     * 
     * @return The keys.
     * @throws PluginException
     *             On key lookup errors.
     */
    protected Keys getKey() throws PluginException {
        Keys k = Keys.valueOf(getName());
        if (k == null) {
            throw new PluginException("Key named '" + getName() + "' not found. Valid values:" + Arrays.toString(Keys.values()));
        }
        return k;
    }
}