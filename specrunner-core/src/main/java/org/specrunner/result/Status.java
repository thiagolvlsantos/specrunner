/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.result;

import org.specrunner.util.xom.AbstractType;

/**
 * A status.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class Status extends AbstractType<Status> {

    /**
     * If it stands for an error.
     */
    protected boolean error;

    /**
     * Creates a status.
     * 
     * @param name
     *            The name.
     * @param importance
     *            The importante.
     * @param error
     *            The error flag.
     */
    protected Status(String name, double importance, boolean error) {
        super(name, importance);
        this.error = error;
    }

    /**
     * true, if stands for an error, false, otherwise.
     * 
     * @return The error flag.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Set error status.
     * 
     * @param error
     *            The error.
     */
    public void setError(boolean error) {
        this.error = error;
    }
}
