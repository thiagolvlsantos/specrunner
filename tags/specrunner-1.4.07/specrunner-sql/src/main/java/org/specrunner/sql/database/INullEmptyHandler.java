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
package org.specrunner.sql.database;

import org.specrunner.sql.meta.EMode;


/**
 * Identifies fields that should be handled as null or empty depending on value
 * and database input or output mode.
 * 
 * @author Thiago Santos.
 * 
 */
public interface INullEmptyHandler {

    /**
     * Check if a value should be handled as null, depending on value and mode.
     * 
     * @param value
     *            The value.
     * @param mode
     *            The mode.
     * @return true, if should be considered null, false, otherwise.
     */
    boolean isNull(String value, EMode mode);

    /**
     * Check if a value should be handled as empty, depending on value and mode.
     * 
     * @param value
     *            The value.
     * @param mode
     *            The mode.
     * @return true, if should be considered empty, false, otherwise.
     */
    boolean isEmpty(String value, EMode mode);
}