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
package org.specrunner.webdriver.impl;

import nu.xom.Element;
import nu.xom.Node;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IWebDriverFactory;

/**
 * Creates a <code>HtmlUnitDriverLocal(true)</code> web driver instance.
 * 
 * @author Thiago Santos
 * 
 */
public class WebDriverFactoryHtmlUnit implements IWebDriverFactory {

    @Override
    public WebDriver create(String name, IContext context) {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Default factory:" + getClass());
        }
        HtmlUnitDriverLocal driver = new HtmlUnitDriverLocal(true);
        driver.setName(name);
        Node node = context.getNode();
        if (node instanceof Element) {
            try {
                UtilParametrized.setProperties(context, driver, (Element) node);
            } catch (PluginException e) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info(e.getMessage(), e);
                }
            }
        }
        driver.initialize();
        return driver;
    }
}