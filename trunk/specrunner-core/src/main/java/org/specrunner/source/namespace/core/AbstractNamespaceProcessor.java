/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.source.namespace.core;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.specrunner.source.namespace.INamespaceInfo;
import org.specrunner.source.namespace.INamespaceProcessor;

/**
 * Basic processor.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractNamespaceProcessor implements INamespaceProcessor {

    /**
     * Prefix for processor.
     */
    private String prefix;
    /**
     * URI for processor.
     */
    private String uri;
    /**
     * Tag to process.
     */
    private String tag;
    /**
     * The XPath context.
     */
    private XPathContext context;

    /**
     * Default.
     * 
     * @param prefix
     *            A prefix.
     * @param uri
     *            A uri.
     * @param tag
     *            A tag name.
     */
    protected AbstractNamespaceProcessor(String prefix, String uri, String tag) {
        this.prefix = prefix;
        this.uri = uri;
        this.tag = tag;
    }

    /**
     * Processor prefix.
     * 
     * @return The prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set prefix.
     * 
     * @param prefix
     *            A prefix.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get URI.
     * 
     * @return The URI.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set URI.
     * 
     * @param uri
     *            The URI.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Get tag.
     * 
     * @return The tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Set tag.
     * 
     * @param tag
     *            A tag.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Get the current context.
     * 
     * @return The context.
     */
    public XPathContext getContext() {
        return context;
    }

    /**
     * Set the context.
     * 
     * @param context
     *            A context.
     */
    public void setContext(XPathContext context) {
        this.context = context;
    }

    @Override
    public void process(INamespaceInfo info, Document document) {
        if (prefix != null && uri == null) {
            uri = info.getURI(prefix);
        }
        if (prefix == null && uri != null) {
            prefix = info.getPrefix(uri);
        }
        context = info.getByURI(prefix);
        if (context == null) {
            context = info.getByURI(uri);
        }
        if (context == null) {
            return;
        }
        process(info, document, prefix == null ? info.getPrefix(uri) : prefix);
    }

    /**
     * Process information.
     * 
     * @param info
     *            Namespace information.
     * @param document
     *            A document.
     * @param prefix
     *            The namespace prefix.
     */
    protected void process(INamespaceInfo info, Document document, String prefix) {
        process(info, document, lookup(document, prefix, tag));
    }

    /**
     * Find nodes.
     * 
     * @param node
     *            A node.
     * @param prefix
     *            The prefix.
     * @param tag
     *            Tag name.
     * @return Nodes satisfying query.
     */
    protected Nodes lookup(Node node, String prefix, String tag) {
        return node.query("descendant::*[@" + prefix + ":" + tag + "]", context);
    }

    /**
     * Process nodes satisfying restriction.
     * 
     * @param info
     *            Information.
     * @param document
     *            Document.
     * @param nodes
     *            Nodes.
     */
    protected abstract void process(INamespaceInfo info, Document document, Nodes nodes);
}