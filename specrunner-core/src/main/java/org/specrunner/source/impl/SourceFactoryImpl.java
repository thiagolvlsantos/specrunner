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
package org.specrunner.source.impl;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.source.IDocumentLoader;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

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
     * Default encoding.
     */
    public static final String DEFAULT_ENCODING = "ISO-8859-1";

    /**
     * Cache of files.
     */
    private static ThreadLocal<ICache<Document>> cache = new ThreadLocal<ICache<Document>>() {
        @Override
        protected ICache<Document> initialValue() {
            return SpecRunnerServices.get(ICacheFactory.class).newCache(SourceFactoryImpl.class.getName());
        };
    };

    /**
     * A builder instance.
     */
    private Builder instance;

    @Override
    public ISource newSource(final InputStream source) throws SourceException {
        return load(source, null);
    }

    @Override
    public ISource newSource(final Reader source) throws SourceException {
        return load(null, source);
    }

    /**
     * Load a source from either an InputStream or a Reader.
     * 
     * @param stream
     *            InputStream.
     * @param reader
     *            Reader.
     * @return The source.
     */
    private ISource load(final InputStream stream, final Reader reader) {
        final Closeable obj = stream != null ? stream : reader;
        return new SourceImpl(null, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                Builder builder = getBuilder();
                try {
                    synchronized (builder) {
                        Document build = stream != null ? builder.build(stream) : builder.build(reader);
                        return addDoctype(build);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (obj != null) {
                        try {
                            obj.close();
                        } catch (IOException e1) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e1.getMessage(), e1);
                            }
                        }
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new SourceException("Could not load the '" + (obj != null ? obj.getClass() : null) + "' source '" + obj + "'.", e);
                }
            }
        });
    }

    @Override
    public ISource newSource(String source) throws SourceException {
        String strTmp = source;
        URI uriTmp = null;
        try {
            uriTmp = new URI(strTmp);
            strTmp = uriTmp.toString();
        } catch (URISyntaxException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        final URI uri = uriTmp;
        final String target = strTmp;
        return new SourceImpl(strTmp, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                long time = System.currentTimeMillis();
                Document result = cache.get().get(target);
                if (result == null) {
                    result = fromTarget(uri, target);
                }
                result = (Document) result.copy();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Load time " + (System.currentTimeMillis() - time) + " ms for: " + target);
                }
                return result;
            }

        });
    }

    /**
     * Load a document from a target.
     * 
     * @param uri
     *            The target corresponding uri (if any).
     * @param target
     *            The target.
     * @return The document, if exists, null, otherwise.
     * @throws SourceException
     *             On load error.
     */
    protected Document fromTarget(URI uri, String target) throws SourceException {
        Document document = null;
        InputStream fin = null;
        InputStream bin = null;
        String charset = null;
        try {
            if (uri == null || !target.startsWith("http")) {
                fin = new FileInputStream(cleanTarget(target));
            } else {
                fin = uri.toURL().openStream();
            }
            bin = new BufferedInputStream(fin);
            // charset
            IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
            charset = (String) fm.get(ISourceFactory.FEATURE_ENCODING);
            if (charset == null) {
                charset = DEFAULT_ENCODING;
            }
            ISource fromReader = newSource(new InputStreamReader(bin, charset));
            document = fromReader.getDocument();
            cache.get().put(target, document);
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
            throw new SourceException("Unsupported charset '" + charset + "'.", e);
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
     * Clean the target name.
     * 
     * @param target
     *            The target.
     * @return The target normalized.
     */
    protected String cleanTarget(String target) {
        return target == null ? target : target.replace("file:///", "").replace("file://", "").replace("file:/", "");
    }

    /**
     * Return the XOM document builder.
     * 
     * @return The builder.
     * @throws SourceException
     *             On builder recover error.
     */
    protected Builder getBuilder() throws SourceException {
        try {
            if (instance == null) {
                instance = new Builder();
            }
            return instance;
        } catch (Exception e) {
            throw new SourceException(e);
        }
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