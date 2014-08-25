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
package org.specrunner.sql.database.impl;

import org.specrunner.sql.database.INullEmptyHandler;
import org.specrunner.sql.meta.EMode;

/**
 * Default implementation. On output verification empty TDs are considered null.
 * In other words, for these TDs the verification against database check if
 * database columns hold null.
 * 
 * @author Thiago Santos
 * 
 */
public class NullEmptyHandlerDefault implements INullEmptyHandler {

    /**
     * Default empty String markup.
     */
    public static final String NULL = "@null";

    /**
     * The empty string mark.
     */
    private String nullVal = NULL;

    /**
     * Default empty String markup.
     */
    public static final String EMPTY = "@empty";

    /**
     * The empty string mark.
     */
    private String emptyVal = EMPTY;

    /**
     * Get null explicit representation.
     * 
     * @return Null representation.
     */
    public String getNullVal() {
        return nullVal;
    }

    /**
     * Set null explicit representation.
     * 
     * @param nullval
     *            Null representation.
     */
    public void setNullVal(String nullval) {
        this.nullVal = nullval;
    }

    /**
     * Return the empty constant.
     * 
     * @return The flag.
     */
    public String getEmptyVal() {
        return emptyVal;
    }

    /**
     * Set empty mark.
     * 
     * @param empty
     *            Empty mark.
     */
    public void setEmptyVal(String empty) {
        this.emptyVal = empty;
    }

    @Override
    public String nullValue(EMode mode) {
        return nullVal;
    }

    @Override
    public String emptyValue(EMode mode) {
        return emptyVal;
    }

    @Override
    public boolean isNull(EMode mode, String value) {
        return value == null || value.equalsIgnoreCase(nullValue(mode)) || (value != null && value.isEmpty() && mode == EMode.OUTPUT);
    }

    @Override
    public boolean isEmpty(EMode mode, String value) {
        return value != null && value.equalsIgnoreCase(emptyValue(mode));
    }
}