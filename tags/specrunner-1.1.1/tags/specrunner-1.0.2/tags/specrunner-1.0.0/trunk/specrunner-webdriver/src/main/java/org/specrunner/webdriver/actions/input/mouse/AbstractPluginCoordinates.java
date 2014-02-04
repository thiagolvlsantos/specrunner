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
package org.specrunner.webdriver.actions.input.mouse;

import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.specrunner.webdriver.AbstractPluginMouse;
import org.specrunner.webdriver.actions.IAction;

public abstract class AbstractPluginCoordinates extends AbstractPluginMouse implements IAction {
    private Point onscreen;
    private Point inviewport;
    private Point indom;
    private Object auxiliary;

    public Point getOnscreen() {
        return onscreen;
    }

    public void setOnscreen(Point onscreen) {
        this.onscreen = onscreen;
    }

    public void setOnscreen(String onscreen) {
        this.onscreen = getPoint(onscreen);
    }

    public Point getInviewport() {
        return inviewport;
    }

    public void setInviewport(Point inviewport) {
        this.inviewport = inviewport;
    }

    public void setInviewport(String inviewport) {
        this.inviewport = getPoint(inviewport);
    }

    public Point getIndom() {
        return indom;
    }

    public void setIndom(Point indom) {
        this.indom = indom;
    }

    public void setIndom(String indom) {
        this.indom = getPoint(indom);
    }

    public Object getAuxiliary() {
        return auxiliary;
    }

    public void setAuxiliary(Object auxiliary) {
        this.auxiliary = auxiliary;
    }

    public static Point getPoint(String point) {
        int pos = point.indexOf(',');
        return new Point(Integer.parseInt(point.substring(0, pos)), Integer.parseInt(point.substring(pos + 1)));
    }

    protected Coordinates getCoordinates() {
        return new Coordinates() {

            @Override
            public Point getLocationOnScreen() {
                return getOnscreen();
            }

            @Override
            public Point getLocationInViewPort() {
                return getInviewport();
            }

            @Override
            public Point getLocationInDOM() {
                return getIndom();
            }

            @Override
            public Object getAuxiliry() {
                return getAuxiliary();
            }
        };
    }
}
