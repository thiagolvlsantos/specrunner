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
package org.specrunner.webdriver.assertions.window;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Window;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.AbstractPluginWindow;

/**
 * Check window position.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginPosition extends AbstractPluginWindow {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

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
            throw new PluginException("PluginPosition assertion requires at least one of attributes 'x' and/or 'y'.");
        }
        if (getX() != null && p != null && !getX().equals(p.getX())) {
            throw new PluginException("X position does not match. Expected:" + getX() + ", received:" + (p != null ? p.getX() : "null"));
        }
        if (getY() != null && p != null && !getY().equals(p.getY())) {
            throw new PluginException("Y position does not match. Expected:" + getY() + ", received:" + (p != null ? p.getY() : "null"));
        }
        result.addResult(Success.INSTANCE, context.peek());
    }
}
