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

import java.text.Normalizer;
import java.util.StringTokenizer;

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

    /**
     * Camel case characters, as Java attributes or methods.
     * 
     * @param text
     *            The text to change.
     * @return The text changed.
     */
    public static String camelCase(String text) {
        return camelCase(text, false);
    }

    /**
     * Normalize a string using camel case rules.
     * 
     * @param text
     *            The text to be normalized.
     * 
     * @param includeFirst
     *            Flag to upper first character also.
     * @return The normalized string.
     */
    public static String camelCase(String text, boolean includeFirst) {
        StringTokenizer st = new StringTokenizer(text, " \t\r\n");
        StringBuilder str = new StringBuilder(includeFirst ? "" : st.nextToken().toLowerCase());
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            str.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                str.append(s.substring(1));
            }
        }
        return clean(str.toString());
    }

    /**
     * Remove all Latin characters.
     * 
     * @param str
     *            The string to be normalized.
     * @return The cleaned string.
     */
    public static String clean(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}