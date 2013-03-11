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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

/**
 * Abstraction for Locatable elements.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginFindSingleLocatable extends AbstractPluginFind {

    /**
     * Method delegation which receives the selected element to be used by
     * subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            A result set.
     * @param client
     *            The browser.
     * @param element
     *            The selected element.
     * @throws PluginException
     *             On execution errors.
     */
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (!(element instanceof Locatable)) {
            throw new PluginException("The given element is not a Locatable instance.");
        }
        process(context, result, client, (Locatable) element);
    }

    /**
     * Method delegation which receives the selected element to be used by
     * subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            A result set.
     * @param client
     *            The browser.
     * @param element
     *            The locatable element.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void process(IContext context, IResultSet result, WebDriver client, Locatable element) throws PluginException;
}