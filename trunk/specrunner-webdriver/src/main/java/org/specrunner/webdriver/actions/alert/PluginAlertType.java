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
package org.specrunner.webdriver.actions.alert;

import nu.xom.Node;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginAlert;
import org.specrunner.webdriver.actions.IAction;

public class PluginAlertType extends AbstractPluginAlert implements IAction {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, TargetLocator target, Alert alert) throws PluginException {
        if (client instanceof HtmlUnitDriver) {
            result.addResult(Status.WARNING, context.peek(), new PluginException("HtmlUnit does not implement alert interactions on version 2.15.0, if a newer version is available try it."));
        } else {
            Node node = context.getNode();
            Object tmp = getValue(getValue() != null ? getValue() : node.getValue(), true, context);
            String str = String.valueOf(tmp);
            alert.sendKeys(str);
            result.addResult(Status.SUCCESS, context.newBlock(node, this));
        }
    }
}
