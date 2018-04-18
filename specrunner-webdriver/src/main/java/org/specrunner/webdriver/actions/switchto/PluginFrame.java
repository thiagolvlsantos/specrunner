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
package org.specrunner.webdriver.actions.switchto;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginSwitchTo;

/**
 * Select a frame by index or nameOrId.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFrame extends AbstractPluginSwitchTo {

    /**
     * Go to frame by index.
     */
    private Integer index;
    /**
     * Go to frame by name or id.
     */
    private String nameorid;

    /**
     * Gets the target frame index.
     * 
     * @return The index.
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets the frame index.
     * 
     * @param index
     *            The frame index.
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Gets the frame name or id.
     * 
     * @return The name or id.
     */
    public String getNameorid() {
        return nameorid;
    }

    /**
     * Sets the target frame name or id.
     * 
     * @param nameorid
     *            The name or id.
     */
    public void setNameorid(String nameorid) {
        this.nameorid = nameorid;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, TargetLocator target) throws PluginException {
        if (index != null) {
            target.frame(index);
        } else if (nameorid != null) {
            target.frame(nameorid);
        } else {
            throw new PluginException(getClass().getSimpleName() + ": requires 'index' or 'nameorid' attribute set.");
        }
        result.addResult(Success.INSTANCE, context.peek());
    }
}
