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
package org.specrunner.webdriver.actions.navigation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.webdriver.AbstractPluginNavigation;

/**
 * Call browser history to a index.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginGo extends AbstractPluginNavigation {

    /**
     * The history index.
     */
    private Long to;

    /**
     * Gets the to index.
     * 
     * @return The index.
     */
    public Long getTo() {
        return to;
    }

    /**
     * Sets the index.
     * 
     * @param to
     *            The new index.
     */
    public void setTo(Long to) {
        this.to = to;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, Navigation history) throws PluginException {
        if (to == null) {
            throw new PluginException("Specify 'to' index. i.e. to='-1' backs one page, to='2' forward history twice.");
        }
        Long index = to;
        while (index < 0) {
            history.back();
            index++;
        }
        while (index > 0) {
            history.forward();
            index--;
        }
    }

}