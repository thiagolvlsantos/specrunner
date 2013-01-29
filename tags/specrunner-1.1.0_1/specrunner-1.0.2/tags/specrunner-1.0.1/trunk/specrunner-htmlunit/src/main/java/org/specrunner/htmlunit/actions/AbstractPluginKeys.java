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
package org.specrunner.htmlunit.actions;

import org.specrunner.htmlunit.AbstractPluginFindSingle;

/**
 * Partial keys implementation.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginKeys extends AbstractPluginFindSingle implements IAction {

    protected Boolean shiftkey;
    protected Boolean ctrlkey;
    protected Boolean altkey;

    public Boolean getShiftkey() {
        return shiftkey;
    }

    public void setShiftkey(Boolean shiftkey) {
        this.shiftkey = shiftkey;
    }

    public Boolean getCtrlkey() {
        return ctrlkey;
    }

    public void setCtrlkey(Boolean ctrlkey) {
        this.ctrlkey = ctrlkey;
    }

    public Boolean getAltkey() {
        return altkey;
    }

    public void setAltkey(Boolean altkey) {
        this.altkey = altkey;
    }
}