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
package org.specrunner.webdriver.assertions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginFind;

/**
 * Check if elements are enabled/not.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginEnabled extends AbstractPluginFind {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException {
        boolean error = false;
        for (WebElement element : elements) {
            if (enabled() != element.isEnabled()) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + getFinderInstance().resume(context) + " should be '" + (enabled() ? "enabled" : "disabled") + "' but is '" + (element.isEnabled() ? "enabled" : "disabled") + "'."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
                error = true;
            }
        }
        if (!error) {
            result.addResult(Success.INSTANCE, context.peek());
        }
    }

    /**
     * Get enabled or not.
     * 
     * @return true, to check enabled, false, otherwise.
     */
    protected abstract boolean enabled();
}
