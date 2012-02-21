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
package org.specrunner.webdriver.actions.window;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Window;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.AbstractPluginWindow;

public class PluginPosition extends AbstractPluginWindow {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, Options options, Window window) throws PluginException {
        Point p = null;
        try {
            p = window.getPosition();
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (getX() == null && getY() == null) {
            throw new PluginException("PluginPosition action requires at least one of attributes 'x' and/or 'y'.");
        }
        if (p != null) {
            window.setPosition(new Point(getX() != null ? getX() : p.getX(), getY() != null ? getY() : p.getY()));
            result.addResult(Status.SUCCESS, context.peek());
        }
    }
}