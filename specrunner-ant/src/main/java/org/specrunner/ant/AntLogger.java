/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.ant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.tools.ant.DefaultLogger;

/**
 * An ant log.
 * 
 * @author Thiago Santos
 * 
 */
public class AntLogger extends DefaultLogger {
    /**
     * Log sink.
     */
    protected ByteArrayOutputStream out;
    /**
     * Stream.
     */
    protected PrintStream print;

    /**
     * Ant log.
     */
    public AntLogger() {
        out = new ByteArrayOutputStream();
        print = new PrintStream(out);
        setErrorPrintStream(print);
        setOutputPrintStream(print);
    }

    /**
     * Get content.
     * 
     * @return The Ant log output.
     * @throws IOException
     *             On reading errors.
     */
    public String getContent() throws IOException {
        try {
            byte[] tmp = out.toByteArray();
            return new String(tmp);
        } finally {
            if (out != null) {
                out.close();
            }
            if (print != null) {
                print.close();
            }
        }
    }
}
