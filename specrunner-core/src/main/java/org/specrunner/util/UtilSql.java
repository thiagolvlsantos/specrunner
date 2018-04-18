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
package org.specrunner.util;

import java.sql.Clob;

/**
 * Utility SQL class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilSql {

    /**
     * Hidden constructor.
     */
    private UtilSql() {
    }

    /**
     * Get string representation of value. If the object is a Clob get its
     * String representation.
     * 
     * @param obj
     *            The object to stringfy.
     * @return A String.
     */
    public static String toStringNullable(Object obj) {
        if (obj == null) {
            return null;
        }
        return toString(obj);
    }

    /**
     * Get string representation of value. If the object is a Clob get its
     * String representation.
     * 
     * @param obj
     *            The object to stringfy.
     * @return A String.
     */
    public static String toString(Object obj) {
        if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            try {
                return UtilIO.getString(clob.getCharacterStream());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return String.valueOf(obj);
    }
}
