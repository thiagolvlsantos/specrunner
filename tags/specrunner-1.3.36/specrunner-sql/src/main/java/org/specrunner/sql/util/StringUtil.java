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
package org.specrunner.sql.util;

import java.util.StringTokenizer;

/**
 * String utils.
 * 
 * @author Thiago Santos
 * 
 */
public final class StringUtil {

    /**
     * Default constructor.
     */
    private StringUtil() {
        // nothing
    }

    /**
     * Tokenize a string.
     * 
     * @param str
     *            The string.
     * @param tokens
     *            The tokens.
     * @return The tokens, using 'separator' as reference.
     */
    public static String[] tokenize(String str, String tokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, tokens);
        String[] result = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            result[i++] = st.nextToken();
        }
        return result;
    }
}
