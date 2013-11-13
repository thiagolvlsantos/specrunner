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
package org.specrunner.webdriver.assertions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginFind;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Check if elements are selected.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginCheckable extends AbstractPluginFind {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException {
        boolean error = false;
        for (WebElement element : elements) {
            if (isCheckbox(element) || isRadio(element)) {
                boolean componentStatus = element.isSelected();
                if (expected() != componentStatus) {
                    result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + getFinderInstance().resume(context) + " should be '" + (expected() ? "checked" : "unchecked") + "' but is '" + (componentStatus ? "checked" : "unchecked") + "'."), new WritablePage(client));
                    error = true;
                }
            } else {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + getFinderInstance().resume(context) + " is not a checkbox or radio is " + element.getTagName()), new WritablePage(client));
                error = true;
            }
        }
        if (!error) {
            result.addResult(Success.INSTANCE, context.peek());
        }
    }

    /**
     * The expected value.
     * 
     * @return true, of expected is marked, false, otherwise.
     */
    protected abstract boolean expected();

    /**
     * Return if a element is a input type=radio.
     * 
     * @param element
     *            The element.
     * @return true, if radio, false otherwise.
     */
    protected boolean isRadio(WebElement element) {
        return "input".equals(element.getTagName()) && "radio".equals(element.getAttribute("type"));
    }

    /**
     * Return if a element is a input type=checkbox.
     * 
     * @param element
     *            The element.
     * @return true, if checkbox, false otherwise.
     */
    protected boolean isCheckbox(WebElement element) {
        return "input".equals(element.getTagName()) && "checkbox".equals(element.getAttribute("type"));
    }

}