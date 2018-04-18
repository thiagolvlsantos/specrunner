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
 * Flick action with xspeed and yspeed.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFlick extends AbstractPluginHasTouchScreen {

    /**
     * X speed.
     */
    private Integer xspeed;
    /**
     * Y speed.
     */
    private Integer yspeed;

    /**
     * Gets the x speed.
     * 
     * @return The x speed.
     */
    public Integer getXspeed() {
        return xspeed;
    }

    /**
     * Sets the X speed.
     * 
     * @param xspeed
     *            The xspeed.
     */
    public void setXspeed(Integer xspeed) {
        this.xspeed = xspeed;
    }

    /**
     * Gets y speed.
     * 
     * @return The y speed.
     */
    public Integer getYspeed() {
        return yspeed;
    }

    /**
     * Sets y speed.
     * 
     * @param yspeed
     *            The y speed.
     */
    public void setYspeed(Integer yspeed) {
        this.yspeed = yspeed;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, TouchScreen touch) throws PluginException {
        touch.flick(getXspeed(), getYspeed());
        result.addResult(Success.INSTANCE, context.peek());
    }
}
