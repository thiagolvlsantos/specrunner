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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.specrunner.SpecRunnerException;
import org.specrunner.util.exceptions.UnstackedException;
import org.specrunner.util.xom.IPresentation;

/**
 * Exception utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilException {

    /**
     * Hidden constructor.
     */
    private UtilException() {
    }

    /**
     * Dump exception trace to a string.
     * 
     * @param exception
     *            The exception.
     * @param fullTrace
     *            Full trace flag.
     * @return The error String representation.
     */
    public static String toString(Throwable exception, Boolean fullTrace) {
        if (!fullTrace || exception instanceof UnstackedException) {
            return exception.getMessage();
        }
        StringWriter sw = null;
        PrintWriter pw = null;
        String result = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            result = sw.toString();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Unwrap SpecRunner subclasses exception to find root cause. If none is
     * found, the most specific exception is returned.
     * 
     * @param error
     *            The error.
     * @return The unwrapped exception.
     */
    public static Throwable unwrapException(Throwable error) {
        while (error instanceof SpecRunnerException) {
            if (error.getCause() != null) {
                error = error.getCause();
            } else {
                break;
            }
        }
        return error;
    }

    /**
     * Unwrap SpecRunner IPresentation to find root cause.
     * 
     * @param error
     *            The error.
     * @return The unwrapped exception.
     */
    public static Throwable unwrapPresentation(Throwable error) {
        Throwable presentation = null;
        Throwable tmp = error;
        while (tmp != null) {
            if (tmp instanceof IPresentation) {
                presentation = tmp;
                break;
            }
            tmp = tmp.getCause();
        }
        return presentation != null ? presentation : unwrapException(error);
    }
}
