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
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;

/**
 * Creates a WebDriver based on contextual information.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWebDriverFactory {

    /**
     * Feature to reuse the first driver instance created by factory.
     */
    String FEATURE_REUSE = IWebDriverFactory.class.getName() + ".reuse";

    /**
     * Create a WebDriver.
     * 
     * @param name
     *            The driver alias.
     * @param context
     *            The context.
     * @return W web driver based on context.
     * @throws PluginException
     *             On creation errors.
     */
    WebDriver create(String name, IContext context) throws PluginException;
}
