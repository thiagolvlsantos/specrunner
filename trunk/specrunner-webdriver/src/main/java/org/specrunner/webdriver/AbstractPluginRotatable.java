/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.webdriver;

import java.util.Arrays;

import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;

/**
 * Partial implementation for rotatable interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginRotatable extends AbstractPluginBrowserAware {

    /**
     * The orientation name.
     */
    private String orientation;

    /**
     * Get the orientation name.
     * 
     * @return The orientation name.
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation.
     * 
     * @param orientation
     *            The orientation.
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets the object orientation by its name.
     * 
     * @return The orientation object.
     * @throws PluginException
     *             On orientation lookup errors.
     */
    protected ScreenOrientation getOrientationValue() throws PluginException {
        ScreenOrientation so = ScreenOrientation.valueOf(getOrientation());
        if (so == null) {
            throw new PluginException("Invalid ScreenOrientation value '" + getOrientation() + "'. Valid values:" + Arrays.toString(ScreenOrientation.values()));
        }
        return so;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        if (client instanceof Rotatable) {
            doEnd(context, result, client, (Rotatable) client);
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("The WebDriver '" + client.getClass().getName() + "' is not a instance of Rotatable."));
        }
    }

    /**
     * Perform an action on rotatable drivers.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param rotatable
     *            The rotatable driver.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, Rotatable rotatable) throws PluginException;
}
