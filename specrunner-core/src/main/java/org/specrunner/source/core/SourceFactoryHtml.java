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
package org.specrunner.source.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;

import nu.xom.Builder;
import nu.xom.Document;

import org.specrunner.SRServices;
import org.specrunner.source.IBuilderFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;

/**
 * The default implementation. Uses a NekoHTML parser under XOM to read the
 * specification. The use of NekoHTML allows using less rigid XML/HTML documents
 * which are fixed by NekoHTML on reading time.
 * 
 * <p>
 * The settings of NekoHTML makes the XML attributes name be in lower case. This
 * poses a restriction on attribute names of plugins, instead of defining in a
 * <code>IPlugin</code> implementation <code>PluginX</code> an attribute named
 * <code>loadOnStart</code>, use <code>loadonstart</code> in small caps.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class SourceFactoryHtml extends AbstractSourceFactory {

    @Override
    protected Document fromTarget(URI uri, String target, String encoding) throws SourceException {
        Document document = null;
        InputStream fin = null;
        InputStream bin = null;
        try {
            if (uri == null || !target.startsWith("http")) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Source from file:" + target);
                }
                fin = new FileInputStream(target);
            } else {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Source from URI:" + uri);
                }
                fin = uri.toURL().openStream();
            }
            bin = new BufferedInputStream(fin);
            document = load(new InputStreamReader(bin, encoding));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Could not load the source '" + target + "'.", e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Unsupported charset '" + encoding + "'.", e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Invalid URI '" + uri + "'.", e);
        } catch (IOException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Reading exception.", e);
        } catch (SourceException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Could not load the source '" + target + "'.", e);
        } finally {
            if (bin != null) {
                try {
                    bin.close();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Close file buffered inputstream:" + target);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
            if (fin != null) {
                try {
                    fin.close();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Close file inputstream:" + target);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        return document;
    }

    /**
     * Load a source from either an InputStream or a Reader.
     * 
     * @param reader
     *            InputStream.
     * @return The source.
     * @throws SourceException
     *             On load error.
     */
    protected Document load(final Reader reader) throws SourceException {
        IBuilderFactory factory = SRServices.get(IBuilderFactory.class);
        Builder builder = factory.newBuilder(new HashMap<String, Object>());
        try {
            synchronized (builder) {
                Document build = builder.build(reader);
                return addDoctype(build);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e1.getMessage(), e1);
                    }
                }
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Could not load the '" + (reader != null ? reader.getClass() : null) + "' source '" + reader + "'.", e);
        }
    }
}