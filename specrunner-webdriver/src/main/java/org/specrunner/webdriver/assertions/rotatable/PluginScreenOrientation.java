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
package org.specrunner.webdriver.assertions.rotatable;

import org.openqa.selenium.Rotatable;
import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginRotatable;

/**
 * Check screen orientation.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginScreenOrientation extends AbstractPluginRotatable {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, Rotatable rotatable) throws PluginException {
        if (rotatable.getOrientation() != getOrientationValue()) {
            throw new PluginException("ScreenOrientation should be '" + getOrientation() + "' but is '" + rotatable.getOrientation() + "'.");
        }
        result.addResult(Success.INSTANCE, context.peek());
    }
}
