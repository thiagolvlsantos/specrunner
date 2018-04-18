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
package org.specrunner.htmlunit.actions;

import org.specrunner.htmlunit.AbstractPluginFindSingle;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Command;

/**
 * Partial keys implementation.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginKeys extends AbstractPluginFindSingle {

    /**
     * The shift key hold status.
     */
    protected Boolean shiftkey;
    /**
     * The ctrl key hold status.
     */
    protected Boolean ctrlkey;
    /**
     * The alty key hold status.
     */
    protected Boolean altkey;

    /**
     * Gets the shift status.
     * 
     * @return true, if shift pressed, false, otherwise.
     */
    public Boolean getShiftkey() {
        return shiftkey;
    }

    /**
     * Sets the shift status.
     * 
     * @param shiftkey
     *            The key status.
     */
    public void setShiftkey(Boolean shiftkey) {
        this.shiftkey = shiftkey;
    }

    /**
     * Gets the ctrl status.
     * 
     * @return true, if ctrl pressed, false, otherwise.
     */
    public Boolean getCtrlkey() {
        return ctrlkey;
    }

    /**
     * Sets the ctrl status.
     * 
     * @param ctrlkey
     *            The key status.
     */
    public void setCtrlkey(Boolean ctrlkey) {
        this.ctrlkey = ctrlkey;
    }

    /**
     * Gets the alt status.
     * 
     * @return true, if alt pressed, false, otherwise.
     */
    public Boolean getAltkey() {
        return altkey;
    }

    /**
     * Sets the alt status.
     * 
     * @param altkey
     *            The key status.
     */
    public void setAltkey(Boolean altkey) {
        this.altkey = altkey;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }
}
