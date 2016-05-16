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
package org.specrunner.webdriver.actions.touch;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.TouchScreen;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginHasTouchScreen;

/**
 * Scroll action with offsets.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginScroll extends AbstractPluginHasTouchScreen {
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
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, TouchScreen touch) throws PluginException {
        touch.scroll(getXoffset(), getYoffset());
        result.addResult(Success.INSTANCE, context.peek());
    }
}
