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
package org.specrunner.webdriver.actions.switchto;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IAction;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Select frame given by element.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginFrameOn extends AbstractPluginFindSingle implements IAction {

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        client.switchTo().frame(element);
        result.addResult(Status.SUCCESS, context.peek());
    }
}