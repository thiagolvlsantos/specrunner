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

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.HtmlUnitDriverLocal;
import org.specrunner.webdriver.IWebDriverFactory;

/**
 * Creates a <code>HtmlUnitDriverLocal(true)</code> web driver instance.
 * 
 * @author Thiago Santos
 * 
 */
public class WebDriverFactoryHtmlUnit implements IWebDriverFactory {

    @Override
    public WebDriver create(IContext context) {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Default factory:" + getClass());
        }
        return new HtmlUnitDriverLocal(true);
    }
}
