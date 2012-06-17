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
package org.specrunner.util;

/**
 * Utility String class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilString {

    /**
     * Hidden constructor.
     */
    private UtilString() {
    }

    /**
     * Normalize a string, removing white spaces.
     * 
     * @param str
     *            The original string.
     * @return The normalized version.
     */
    public static String normalize(String str) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (!Character.isWhitespace(c) && Character.isDefined(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}