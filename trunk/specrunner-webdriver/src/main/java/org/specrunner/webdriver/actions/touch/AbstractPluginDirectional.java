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
package org.specrunner.webdriver.actions.touch;

import org.specrunner.plugins.IAction;
import org.specrunner.webdriver.AbstractPluginHasTouchScreen;

/**
 * Touch screen with starting action point.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginDirectional extends AbstractPluginHasTouchScreen implements IAction {

    /**
     * The x position.
     */
    private Integer x;
    /**
     * The y position.
     */
    private Integer y;

    /**
     * Gets the x position.
     * 
     * @return The x position.
     */
    public Integer getX() {
        return x;
    }

    /**
     * Sets the x position.
     * 
     * @param x
     *            The x position.
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Gets the y position.
     * 
     * @return The y position.
     */
    public Integer getY() {
        return y;
    }

    /**
     * Sets the y position.
     * 
     * @param y
     *            The y position.
     */
    public void setY(Integer y) {
        this.y = y;
    }
}