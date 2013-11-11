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

import java.net.URI;
import java.net.URISyntaxException;

import nu.xom.DocType;
import nu.xom.Document;

import org.specrunner.SRServices;
import org.specrunner.source.IDocumentLoader;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Abstraction to readers.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractSourceFactory extends EncodedImpl implements ISourceFactory {

    /**
     * Cache of files.
     */
    private static ICache<String, Document> cache = SRServices.get(ICacheFactory.class).newCache(AbstractSourceFactory.class.getName());

    @Override
    public void initialize() {
        // empty
    }

    @Override
    public ISource newSource(Object source) throws SourceException {
        String strTmp = String.valueOf(source);
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
        return new SourceImpl(getEncoding(), strTmp, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                long time = System.currentTimeMillis();
                Document result = null;
                synchronized (cache) {
                    result = cache.get(target);
                    if (result == null) {
                        result = fromTarget(uri, cleanTarget(target), getEncoding());
                        addDoctype(result);
                        cache.put(target, result);
                    }
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
     * Load file from source.
     * 
     * @param uri
     *            The source URI.
     * @param target
     *            The source name.
     * @param encoding
     *            The encoding.
     * @return The document.
     * @throws SourceException
     *             On load errors.
     */
    protected abstract Document fromTarget(URI uri, String target, String encoding) throws SourceException;

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
