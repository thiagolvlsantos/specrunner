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
package org.specrunner.source.core;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.SRServices;
import org.specrunner.source.IDocumentLoader;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.source.namespace.INamespaceInfo;
import org.specrunner.source.namespace.INamespaceLoader;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.source.resource.IResourceManagerFactory;
import org.specrunner.util.UtilLog;

/**
 * The default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceImpl implements ISource {

    /**
     * Input source encoding.
     */
    protected String encoding;
    /**
     * The source as String.
     */
    protected String string;
    /**
     * The source as file.
     */
    protected File file;
    /**
     * The source as URI.
     */
    protected URI uri;
    /**
     * The source factory used to create it.
     */
    protected ISourceFactory factory;
    /**
     * The resource manager.
     */
    protected IResourceManager manager;
    /**
     * A document loader.
     */
    protected IDocumentLoader loader;
    /**
     * The document.
     */
    protected Document document;
    /**
     * A namespace loader.
     */
    protected INamespaceLoader nsloader;
    /**
     * The namespace info.
     */
    protected INamespaceInfo namespaceInfo;

    /**
     * Creates a source instance.
     * 
     * @param encoding
     *            The source encoding.
     * @param string
     *            The source reference.
     * @param factory
     *            The factory.
     * @param loader
     *            The document loader.
     * @param nsloader
     *            The namespace information loader.
     */
    public SourceImpl(String encoding, String string, ISourceFactory factory, IDocumentLoader loader, INamespaceLoader nsloader) {
        this.encoding = encoding;
        this.string = string;
        if (this.string != null) {
            this.file = new File(this.string);
            if (this.file.exists()) {
                this.string = this.file.toURI().toString();
            } else {
                this.file = null;
            }
            try {
                this.uri = new URI(this.string);
                if (!this.string.startsWith("http") && this.file == null) {
                    this.file = new File(this.string.replace(":", ""));
                }
            } catch (URISyntaxException e) {
                this.uri = this.file.toURI();
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
            }
        }
        this.factory = factory;
        this.loader = loader;
        this.nsloader = nsloader;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public ISourceFactory getFactory() {
        return factory;
    }

    @Override
    public ISource resolve(ISource other) throws SourceException {
        URI uriBase = getURI();
        if (uriBase == null) {
            return null;
        }
        URI uriTmp = uriBase.resolve(other.getString());
        return factory.newSource(uriTmp.toString());
    }

    @Override
    public String relative(ISource other) {
        String a = getURI().toString();
        String b = other.getURI().toString();
        int i = 0;
        while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i)) {
            i++;
        }
        return a.substring(i);
    }

    @Override
    public IResourceManager getManager() {
        if (manager == null) {
            manager = SRServices.get(IResourceManagerFactory.class).newManager(this);
        }
        return manager;
    }

    @Override
    public Document getDocument() throws SourceException {
        if (document == null) {
            document = loader.load();
        }
        return document;
    }

    @Override
    public INamespaceInfo getNamespaceInfo() throws SourceException {
        if (namespaceInfo == null) {
            namespaceInfo = nsloader.load(getDocument());
        }
        return namespaceInfo;
    }

    @Override
    public List<Element> getScenarios() throws SourceException {
        Document d = getDocument();
        Nodes ns = d.query("//*[contains(@class,'scenario')] | //scenario");
        return ns.size() > 0 ? toElements(ns) : Arrays.asList(d.getRootElement());
    }

    /**
     * Convert to a sequence of elements.
     * 
     * @param ns
     *            Nodes.
     * @return A list of elements.
     */
    protected List<Element> toElements(Nodes ns) {
        List<Element> result = new LinkedList<Element>();
        for (int i = 0; i < ns.size(); i++) {
            result.add((Element) ns.get(i));
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((string == null) ? 0 : string.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ISource other = (ISource) obj;
        if (getURI() == null) {
            if (other.getURI() != null) {
                return false;
            }
        } else if (!getURI().equals(other.getURI())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getString() + "," + getURI();
    }
}
