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