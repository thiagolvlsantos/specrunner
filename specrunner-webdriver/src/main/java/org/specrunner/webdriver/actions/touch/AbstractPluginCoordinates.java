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
package org.specrunner.webdriver.actions.touch;

import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Command;
import org.specrunner.webdriver.AbstractPluginHasTouchScreen;

/**
 * Touch screen helper (by coordinates).
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginCoordinates extends AbstractPluginHasTouchScreen {
    /**
     * Screen point.
     */
    private Point onscreen;
    /**
     * Point on view port.
     */
    private Point inviewport;
    /**
     * Point in DOM.
     */
    private Point indom;
    /**
     * Auxiliary.
     */
    private Object auxiliary;

    /**
     * Gets the point on screen.
     * 
     * @return The point.
     */
    public Point getOnscreen() {
        return onscreen;
    }

    /**
     * Sets point.
     * 
     * @param onscreen
     *            The point.
     */
    public void setOnscreen(Point onscreen) {
        this.onscreen = onscreen;
    }

    /**
     * Sets the on screen point as string.
     * 
     * @param onscreen
     *            The screen point.
     */
    public void setOnscreen(String onscreen) {
        this.onscreen = getPoint(onscreen);
    }

    /**
     * Gets the point in view.
     * 
     * @return The point.
     */
    public Point getInviewport() {
        return inviewport;
    }

    /**
     * Sets the point in view.
     * 
     * @param inviewport
     *            The point in view.
     */
    public void setInviewport(Point inviewport) {
        this.inviewport = inviewport;
    }

    /**
     * Sets the view port.
     * 
     * @param inviewport
     *            The view port as string.
     */
    public void setInviewport(String inviewport) {
        this.inviewport = getPoint(inviewport);
    }

    /**
     * Gets the DOM point.
     * 
     * @return The DOM point.
     */
    public Point getIndom() {
        return indom;
    }

    /**
     * Sets the DOM point.
     * 
     * @param indom
     *            The DOM point.
     */
    public void setIndom(Point indom) {
        this.indom = indom;
    }

    /**
     * The DOM point as string.
     * 
     * @param indom
     *            The DOM point.
     */
    public void setIndom(String indom) {
        this.indom = getPoint(indom);
    }

    /**
     * Gets the auxiliary.
     * 
     * @return The auxiliary.
     */
    public Object getAuxiliary() {
        return auxiliary;
    }

    /**
     * Sets the auxiliary.
     * 
     * @param auxiliary
     *            The auxiliary.
     */
    public void setAuxiliary(Object auxiliary) {
        this.auxiliary = auxiliary;
    }

    /**
     * Gets the point from a string, the pattern is 'x,y'.
     * 
     * @param point
     *            The point as string.
     * @return The point.
     */
    public static Point getPoint(String point) {
        int pos = point.indexOf(',');
        return new Point(Integer.parseInt(point.substring(0, pos)), Integer.parseInt(point.substring(pos + 1)));
    }

    /**
     * Builder of coordinates.
     * 
     * @return The coordinates.
     */
    protected Coordinates getCoordinates() {
        return new Coordinates() {

            @Override
            public Point onScreen() {
                return getOnscreen();
            }

            @Override
            public Point inViewPort() {
                return getInviewport();
            }

            @Override
            public Point onPage() {
                return getIndom();
            }

            @Override
            public Object getAuxiliary() {
                return AbstractPluginCoordinates.this.getAuxiliary();
            }

        };
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }
}
