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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.source.resource.ResourceException;

/**
 * IO utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilIO {

    /**
     * Reading buffer size.
     */
    public static final int BUFFER_SIZE = 1024;
    /**
     * Temp reading.
     */
    private static final int READ_SIZE = 12;

    /**
     * Hidden constructor.
     */
    private UtilIO() {
    }

    /**
     * Write all contents to a output.
     * 
     * @param files
     *            The input list.
     * @param out
     *            The output.
     * @throws ResourceException
     *             On writing errors.
     */
    public static void writeAllTo(List<URL> files, OutputStream out) throws ResourceException {
        InputStream[] ins = null;
        int i = 0;
        try {
            ins = getInputStreams(files);
            for (InputStream in : ins) {
                writeTo(in, out);
                in.close();
                i++;
            }
        } catch (IOException e) {
            if (ins != null) {
                for (int j = 0; j < i; j++) {
                    try {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("Closing " + ins[j]);
                        }
                        ins[j].close();
                    } catch (IOException e1) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e1.getMessage(), e1);
                        }
                    }
                }
            }

            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    /**
     * Get all input streams for the given URL list.
     * 
     * @param files
     *            The files.
     * @return The input stream list.
     * @throws ResourceException
     *             On loading errors.
     */
    public static InputStream[] getInputStreams(List<URL> files) throws ResourceException {
        InputStream[] result = new InputStream[files.size()];
        int i = 0;
        try {
            for (URL url : files) {
                result[i++] = url.openStream();
            }
        } catch (IOException e) {
            for (int j = 0; j < i; j++) {
                try {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Closing " + files.get(j));
                    }
                    result[j].close();
                } catch (IOException e1) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e1.getMessage(), e1);
                    }
                }
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new ResourceException(e);
        }
        return result;
    }

    /**
     * Write a input stream to a output.
     * 
     * @param in
     *            The input.
     * @param out
     *            The output.
     * @throws IOException
     *             On writing errors.
     */
    public static void writeTo(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int size = 0;
        while ((size = in.read(buffer)) > 0) {
            out.write(buffer, 0, size);
        }
        out.flush();
    }

    /**
     * Write the url input to the file output.
     * 
     * @param url
     *            The input.
     * @param file
     *            The output.
     * @throws IOException
     *             On writing errors.
     */
    public static void writeToClose(URL url, File file) throws IOException {
        InputStream in = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            in = url.openStream();
            fout = new FileOutputStream(file);
            bout = new BufferedOutputStream(fout);
            writeTo(in, bout);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Closing " + in);
                    }
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Closing " + fout);
                    }
                }
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Closing " + bout);
                    }
                }
            }
        }
    }

    /**
     * Request for key interaction.
     * 
     * @throws IOException
     *             On errors.
     */
    public static void pressKey() throws IOException {
        IConcurrentMapping cm = SpecRunnerServices.get(IConcurrentMapping.class);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("(" + cm.getThread() + ") read keybord (press 'Enter' to go on)...");
        }
        System.in.read(new byte[READ_SIZE]);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("(" + cm.getThread() + ") done...");
        }
    }
}
