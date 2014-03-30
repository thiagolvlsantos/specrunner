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
package org.specrunner.webdriver.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginFind;

/**
 * Partial implementation of actions in checkboxes.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginCheck extends AbstractPluginFind {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException {
        boolean success = true;
        for (int i = 0; i < elements.length; i++) {
            WebElement e = elements[i];
            if (!isCheckbox(e)) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + e + " is not a checkbox."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
                success = false;
                break;
            }
            doSomething(e);
        }
        if (success) {
            result.addResult(Success.INSTANCE, context.peek());
        }
    }

    /**
     * Check if is checkbox.
     * 
     * @param element
     *            The element.
     * @return true, if checkbox, false, otherwise.
     */
    protected boolean isCheckbox(WebElement element) {
        return "input".equals(element.getTagName()) && "checkbox".equals(element.getAttribute("type"));
    }

    /**
     * Perform something with the checkbox.
     * 
     * @param checkbox
     *            The element.
     */
    protected abstract void doSomething(WebElement checkbox);

}