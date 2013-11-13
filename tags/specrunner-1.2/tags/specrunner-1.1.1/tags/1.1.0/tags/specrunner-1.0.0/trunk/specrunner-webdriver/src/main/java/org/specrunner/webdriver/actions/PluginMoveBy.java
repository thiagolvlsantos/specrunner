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
package org.specrunner.webdriver.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Move to a given element. With or without offset.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginMoveBy extends AbstractPluginFindSingle implements IAction {

    private Integer xoffset;
    private Integer yoffset;

    public Integer getXoffset() {
        return xoffset;
    }

    public void setXoffset(Integer xoffset) {
        this.xoffset = xoffset;
    }

    public Integer getYoffset() {
        return yoffset;
    }

    public void setYoffset(Integer yoffset) {
        this.yoffset = yoffset;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (getXoffset() == null && getYoffset() == null) {
            throw new PluginException("PluginMoveBy requires xoffset and yoffset attributes.");
        }
        Action ac = new Actions(client).moveByOffset(getXoffset(), getYoffset()).build();
        ac.perform();
        result.addResult(Status.SUCCESS, context.peek());
    }
}