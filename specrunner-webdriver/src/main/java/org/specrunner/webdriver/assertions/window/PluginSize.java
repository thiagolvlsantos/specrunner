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
package org.specrunner.webdriver.assertions.window;

import org.openqa.selenium.Dimension;
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
 * Check size of a window.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSize extends AbstractPluginWindow {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, Options options, Window window) throws PluginException {
        Dimension d = null;
        try {
            d = window.getSize();
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (getWidth() == null && getHeight() == null) {
            throw new PluginException("PluginSize assertion requires at least one of attributes 'width' and/or 'height'.");
        }
        if (getWidth() != null && d != null && !getWidth().equals(d.getWidth())) {
            throw new PluginException("Width size does not match. Expected:" + getWidth() + ", received:" + (d != null ? d.getWidth() : "null"));
        }
        if (getHeight() != null && d != null && !getHeight().equals(d.getHeight())) {
            throw new PluginException("Height size does not match. Expected:" + getHeight() + ", received:" + (d != null ? d.getHeight() : "null"));
        }
        result.addResult(Success.INSTANCE, context.peek());
    }
}
