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
import org.openqa.selenium.WebElement;

/**
 * Something that prepares for actions.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPrepare {

    /**
     * Feature for prepare.
     */
    String FEATURE_PREPARE = IPrepare.class.getName() + ".prepare";

    /**
     * Prepare elements to interact with.
     * 
     * @param source
     *            Source of interactions.
     * @param client
     *            A driver.
     * @param elements
     *            Elements to prepare.
     */
    void prepare(AbstractPluginFind source, WebDriver client, WebElement... elements);
}
