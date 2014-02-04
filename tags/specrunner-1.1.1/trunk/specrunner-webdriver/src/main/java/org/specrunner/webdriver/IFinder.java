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

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.parameters.IParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

/**
 * Something that finds WebElement in pages.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFinder extends IParameterHolder {

    /**
     * Clear any previous finder settings.
     */
    void reset();

    /**
     * Returns a list of elements.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param client
     *            The browser.
     * @return A list empty or not of elements.
     * @throws PluginException
     *             On filter errors.
     */
    List<WebElement> find(IContext context, IResultSet result, WebDriver client) throws PluginException;

    /**
     * The representation of the filter.
     * 
     * @param context
     *            The test context.
     * @return A string that 'explain' the search used.
     * @throws PluginException
     *             On detail errors.
     */
    String resume(IContext context) throws PluginException;
}