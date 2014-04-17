/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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

import java.io.IOException;
import java.io.Reader;
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
     * Get string representation of value. If the object is a Blob get its
     * String representation.
     * 
     * @param obj
     *            The object to stringfy.
     * @return A String.
     */
    public static String toString(Object obj) {
        if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            Reader in = null;
            try {
                in = clob.getCharacterStream();
                if (in != null) {
                    StringBuilder sb = new StringBuilder();
                    int i;
                    while ((i = in.read()) != -1) {
                        sb.append((char) i);
                    }
                    return sb.toString();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return String.valueOf(obj);
    }
}