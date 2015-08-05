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
package org.specrunner.webdriver.actions.alert;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.result.status.Warning;
import org.specrunner.webdriver.AbstractPluginAlert;

/**
 * Perform a alert dismiss action.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginAlertDismiss extends AbstractPluginAlert {
    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, TargetLocator target, Alert alert) throws PluginException {
        if (client instanceof HtmlUnitDriver) {
            result.addResult(Warning.INSTANCE, context.peek(), new PluginException("HtmlUnit does not implement alert interactions on version 2.15.0, if a newer version is available try it."));
        } else {
            alert.dismiss();
            result.addResult(Success.INSTANCE, context.peek());
        }
    }
}
