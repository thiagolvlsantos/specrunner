package org.specrunner.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {

    private ExceptionUtil() {
    }

    public static String dumpException(Throwable exception) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.close();
        String result = sw.toString();
        sw.close();
        return result;
    }
}