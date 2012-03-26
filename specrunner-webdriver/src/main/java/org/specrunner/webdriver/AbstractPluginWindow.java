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
package org.specrunner.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Window;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

/**
 * Partial implementation for windows interactions.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginWindow extends AbstractPluginOptions {

    /**
     * X point.
     */
    private Integer x;
    /**
     * Y point.
     */
    private Integer y;
    /**
     * Window width.
     */
    private Integer width;
    /**
     * Window height.
     */
    private Integer height;

    /**
     * Gets the X position.
     * 
     * @return The X position.
     */
    public Integer getX() {
        return x;
    }

    /**
     * Set the X position.
     * 
     * @param x
     *            The X position.
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Gets the Y position.
     * 
     * @return The Y position.
     */
    public Integer getY() {
        return y;
    }

    /**
     * Sets the Y position.
     * 
     * @param y
     *            The Y position.
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * Gets the client width.
     * 
     * @return The width.
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the client width.
     * 
     * @param width
     *            The width.
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * Gets the client height.
     * 
     * @return The height.
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Sets the client height.
     * 
     * @param height
     *            The height.
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client, Options options) throws PluginException {
        doEnd(context, result, client, options, options.window());
        result.addResult(Status.SUCCESS, context.peek());
    }

    /**
     * Perform an option/window action.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param options
     *            The options.
     * @param window
     *            The window.
     * @throws PluginException
     *             On processing errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebDriver client, Options options, Window window) throws PluginException;
}