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
package org.specrunner.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.source.core.UtilEncoding;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;
import org.specrunner.util.resources.ResourceFinder;

/**
 * IO utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilIO {

    /**
     * Cache of resources. Avoid unnecessary file/network/jar accesses.
     */
    protected static ICache<URL, byte[]> cache = SRServices.get(ICacheFactory.class).newCache(UtilIO.class.getName());

    /**
     * Reading buffer size.
     */
    public static final int BUFFER_SIZE = 16 * 1024;
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
     * Read uncommented not empty lines from files.
     * 
     * @param filename
     *            A file name.
     * @return The file content as a list.
     * @throws ResourceException
     *             On load errors.
     */
    public static List<String> readLines(String filename) throws ResourceException {
        List<URL> files;
        try {
            files = SRServices.get(ResourceFinder.class).initilize().getAllResources(filename);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
        List<String> result = new LinkedList<String>();
        InputStream[] ins = getInputStreams(files);
        for (InputStream i : ins) {
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                isr = new InputStreamReader(i, UtilEncoding.getEncoding());
                br = new BufferedReader(isr, BUFFER_SIZE);
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    tmp = tmp.trim();
                    if (tmp.startsWith("#") || tmp.isEmpty()) {
                        continue;
                    }
                    result.add(tmp);
                }
            } catch (UnsupportedEncodingException e) {
                throw new ResourceException(e);
            } catch (IOException e) {
                throw new ResourceException(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new ResourceException(e);
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        throw new ResourceException(e);
                    }
                }
            }
        }
        return result;
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
                ins[i] = null;
                i++;
            }
        } catch (IOException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        } finally {
            if (ins != null) {
                for (int j = 0; j < i; j++) {
                    try {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug("Closing " + ins[j]);
                        }
                        if (ins[j] != null) {
                            ins[j].close();
                        }
                    } catch (IOException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                    }
                }
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
                result[i++] = getStream(url);
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
     * Get stream for a given URI.
     * 
     * @param uri
     *            An uri.
     * @return A stream.
     * @throws IOException
     *             On load errors.
     */
    public static InputStream getStream(URI uri) throws IOException {
        return getStream(uri.toURL());
    }

    /**
     * Get stream for a given URL.
     * 
     * @param url
     *            An url.
     * @return A stream.
     * @throws IOException
     *             On load errors.
     */
    public static InputStream getStream(URL url) throws IOException {
        synchronized (cache) {
            byte[] data = cache.get(url);
            if (data == null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Stream open: " + url);
                }
                data = getBytes(url.openStream());
                cache.put(url, data);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Stream with '" + data.length + "' bytes cached for: " + url);
                }
            } else {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Stream reused for: " + url);
                }
            }
            return new ByteArrayInputStream(data);
        }
    }

    /**
     * Get Reader for a given URI.
     * 
     * @param uri
     *            An uri.
     * @return A reader.
     * @throws IOException
     *             On load errors.
     */
    public static Reader getReader(URI uri) throws IOException {
        return getReader(uri.toURL());
    }

    /**
     * Get Reader for a given URL.
     * 
     * @param url
     *            An url.
     * @return A reader.
     * @throws IOException
     *             On load errors.
     */
    public static Reader getReader(URL url) throws IOException {
        return new InputStreamReader(getStream(url), Charset.forName(UtilEncoding.getEncoding()));
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
     * Write a reader to a writer.
     * 
     * @param in
     *            The input.
     * @param out
     *            The output.
     * @throws IOException
     *             On writing errors.
     */
    public static void writeTo(Reader in, Writer out) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int size = 0;
        while ((size = in.read(buffer)) > 0) {
            out.write(buffer, 0, size);
        }
        out.flush();
    }

    /**
     * Write the URL input to the file output.
     * 
     * @param url
     *            The input.
     * @param file
     *            The output.
     * @throws IOException
     *             On writing errors.
     */
    public static void writeToClose(URL url, File file) throws IOException {
        long time = System.currentTimeMillis();
        InputStream in = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            in = getStream(url);
            fout = new FileOutputStream(file);
            bout = new BufferedOutputStream(fout);
            writeTo(in, bout);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Closing " + in, e);
                    }
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Closing " + fout, e);
                    }
                }
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Closing " + bout, e);
                    }
                }
            }
        }
        if (UtilLog.LOG.isDebugEnabled()) {
            time = System.currentTimeMillis() - time;
            UtilLog.LOG.debug("After writeToClose time:" + time + ".");
        }
    }

    /**
     * Request for key interaction.
     * 
     * @throws IOException
     *             On errors.
     */
    public static void pressKey() throws IOException {
        IConcurrentMapping cm = SRServices.get(IConcurrentMapping.class);
        IOutput output = SRServices.get(IOutputFactory.class).currentOutput();
        output.println("(" + cm.getThread() + ") read keybord (press 'Enter' to go on)...");
        System.in.read(new byte[READ_SIZE]);
        output.println("(" + cm.getThread() + ") done...");
    }

    public static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        FileInputStream fr = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fr = new FileInputStream(file);
            isr = new InputStreamReader(fr, UtilEncoding.getEncoding());
            br = new BufferedReader(isr);
            String input = br.readLine();
            while (input != null) {
                sb.append(input);
                input = br.readLine();
                if (input != null) {
                    sb.append('\n');
                }
            }
        } finally {
            if (fr != null) {
                fr.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return sb.toString();
    }

    public static String getString(Reader in) throws IOException {
        if (in == null) {
            return null;
        }
        String result = null;
        try {
            StringWriter out = new StringWriter();
            try {
                writeTo(in, out);
                result = out.toString();
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
        return result;
    }

    public static byte[] getBytes(Reader in, Charset charset) throws IOException {
        if (in == null) {
            return null;
        }
        return getString(in).getBytes(charset);
    }

    public static byte[] getBytes(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        byte[] result = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                writeTo(in, out);
                result = out.toByteArray();
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
        return result;
    }
}