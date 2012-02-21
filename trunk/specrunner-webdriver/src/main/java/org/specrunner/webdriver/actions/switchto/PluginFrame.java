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
package org.specrunner.webdriver.actions.switchto;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginSwitchTo;

/**
 * Select a frame by index or nameOrId.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginFrame extends AbstractPluginSwitchTo {

    private Integer index;
    private String nameorid;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getNameorid() {
        return nameorid;
    }

    public void setNameorid(String nameorid) {
        this.nameorid = nameorid;
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
        result.addResult(Status.SUCCESS, context.peek());
    }
}