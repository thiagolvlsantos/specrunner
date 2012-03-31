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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class ExceptionUtil {

    /**
     * Hidden constructor.
     */
    private ExceptionUtil() {
    }

    /**
     * Dump exception trace to a string.
     * 
     * @param exception
     *            The exception.
     * @return The error String representation.
     * @throws IOException
     *             On dump errors.
     */
    public static String toString(Throwable exception) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.close();
        String result = sw.toString();
        sw.close();
        return result;
    }
}