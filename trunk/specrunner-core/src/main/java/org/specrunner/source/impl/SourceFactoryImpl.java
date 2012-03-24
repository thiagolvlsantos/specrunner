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
package org.specrunner.source.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.specrunner.source.IDocumentLoader;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;

/**
 * The default implementation. Uses a NekoHTML reader, e XOM to read the
 * specification. The use of NekoHTML allows using less rigid XML/HTML documents
 * which are fixed on reading time.
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
public class SourceFactoryImpl implements ISourceFactory {
    /**
     * Builder of XOM documents.
     */
    private Builder builder;

    /**
     * Creates a new source factory using XOM resources.
     */
    public SourceFactoryImpl() {
        try {
            HTMLConfiguration config = new HTMLConfiguration();
            SAXParserLocal neko = new SAXParserLocal(config);
            neko.setFeature("http://xml.org/sax/features/namespaces", false);
            neko.setFeature("http://cyberneko.org/html/features/override-namespaces", false);
            neko.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            neko.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
            neko.setProperty("http://cyberneko.org/html/properties/default-encoding", "ISO-8859-1");
            builder = new Builder(neko, true);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Local SAX configuration.
     * 
     * @author Thiago Santos
     * 
     */
    public static class SAXParserLocal extends AbstractSAXParser {
        /**
         * Default settings.
         * 
         * @param config
         *            The configuration.
         */
        public SAXParserLocal(HTMLConfiguration config) {
            super(config);
        }
    }

    @Override
    public ISource newSource(final InputStream source) throws SourceException {
        return new SourceImpl(null, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                try {
                    synchronized (builder) {
                        return addDoctype(builder.build(source));
                    }
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new SourceException("Could not load the 'InputStream' source '" + source + "'.", e);
                }
            }
        });
    }

    @Override
    public ISource newSource(final Reader source) throws SourceException {
        return new SourceImpl(null, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                try {
                    synchronized (builder) {
                        return addDoctype(builder.build(source));
                    }
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new SourceException("Could not load the 'Reader' source '" + source + "'.", e);
                }
            }
        });
    }

    @Override
    public ISource newSource(String source) throws SourceException {
        URI u = null;
        try {
            u = new URI(source);
            source = u.toString();
        } catch (URISyntaxException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        final URI uri = u;
        final String target = source;
        return new SourceImpl(source, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                if (uri != null) {
                    try {
                        synchronized (builder) {
                            return addDoctype(builder.build(target));
                        }
                    } catch (Exception e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        throw new SourceException("Could not load the 'String/URI' source '" + target + "'.", e);
                    }
                } else {
                    FileReader reader = null;
                    try {
                        reader = new FileReader(target);
                        return newSource(reader).getDocument();
                    } catch (FileNotFoundException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                        throw new SourceException("Could not load the 'String/File' source '" + target + "'.", e);
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                                if (UtilLog.LOG.isDebugEnabled()) {
                                    UtilLog.LOG.debug("Close file reader:" + target);
                                }
                            } catch (IOException e) {
                                if (UtilLog.LOG.isTraceEnabled()) {
                                    UtilLog.LOG.trace(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Adds the XHTML Doctype to the document if none is specified.
     * 
     * @param document
     *            The document.
     * @return The document itself.
     */
    protected Document addDoctype(Document document) {
        if (document.getDocType() == null) {
            // <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
            DocType dt = new DocType("html", "-//W3C//DTD XHTML 1.0 Transitional//EN", "");
            document.insertChild(dt, 0);
        }
        return document;
    }
}