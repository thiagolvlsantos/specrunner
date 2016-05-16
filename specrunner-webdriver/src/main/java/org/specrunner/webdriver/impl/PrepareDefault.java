/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.AbstractPluginFind;
import org.specrunner.webdriver.IPrepare;

/**
 * Prepare default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class PrepareDefault implements IPrepare {

    @Override
    public void prepare(AbstractPluginFind source, WebDriver client, WebElement... elements) {
        if (client instanceof JavascriptExecutor) {
            JavascriptExecutor executor = (JavascriptExecutor) client;
            if (elements != null) {
                for (WebElement e : elements) {
                    if (e != null) {
                        try {
                            Point location = e.getLocation();
                            if (location != null) {
                                executor.executeScript("arguments[0].focus()", e);
                                executor.executeScript("window.scrollTo(" + location.getX() + "," + location.getY() + ")");
                            }
                        } catch (WebDriverException ex) {
                            if (UtilLog.LOG.isTraceEnabled()) {
                                UtilLog.LOG.trace(ex.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }
}
