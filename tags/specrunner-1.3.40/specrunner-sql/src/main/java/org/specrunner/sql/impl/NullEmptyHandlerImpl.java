/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.sql.impl;

import org.specrunner.sql.EMode;
import org.specrunner.sql.INullEmptyHandler;

/**
 * Default implementation. On output verification empty TDs are considered null.
 * In other words, for these TDs the verification against database check if
 * database columns hold null.
 * 
 * @author Thiago Santos
 * 
 */
public class NullEmptyHandlerImpl implements INullEmptyHandler {

    /**
     * Default empty String markup.
     */
    public static final String EMPTY = "@e";

    /**
     * The empty string mark.
     */
    private String empty = EMPTY;

    /**
     * Return the empty constant.
     * 
     * @return The flag.
     */
    public String getEmpty() {
        return empty;
    }

    /**
     * Set empty mark.
     * 
     * @param empty
     *            Empty mark.
     */
    public void setEmpty(String empty) {
        this.empty = empty;
    }

    @Override
    public boolean isNull(String value, EMode mode) {
        return value != null && value.isEmpty() && mode == EMode.OUTPUT;
    }

    @Override
    public boolean isEmpty(String value, EMode mode) {
        return value != null && value.equalsIgnoreCase(empty);
    }
}