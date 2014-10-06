/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
import org.specrunner.plugins.ActionType;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Move to a given element. With or without offset.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginMoveBy extends AbstractPluginFindSingle {

    /**
     * The x offset gap.
     */
    private Integer xoffset;
    /**
     * The y offset gap.
     */
    private Integer yoffset;

    /**
     * Gets the x offset value.
     * 
     * @return The x offset.
     */
    public Integer getXoffset() {
        return xoffset;
    }

    /**
     * Sets the x offset value.
     * 
     * @param xoffset
     *            The offset.
     */
    public void setXoffset(Integer xoffset) {
        this.xoffset = xoffset;
    }

    /**
     * Gets the y offset method.
     * 
     * @return The y offset.
     */
    public Integer getYoffset() {
        return yoffset;
    }

    /**
     * Sets the y offset.
     * 
     * @param yoffset
     *            The y offset.
     */
    public void setYoffset(Integer yoffset) {
        this.yoffset = yoffset;
    }

    @Override
    public ActionType getActionType() {
        return org.specrunner.plugins.type.Command.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (getXoffset() == null && getYoffset() == null) {
            throw new PluginException("PluginMoveBy requires xoffset and yoffset attributes.");
        }
        Action ac = new Actions(client).moveByOffset(getXoffset(), getYoffset()).build();
        ac.perform();
        result.addResult(Success.INSTANCE, context.peek());
    }
}