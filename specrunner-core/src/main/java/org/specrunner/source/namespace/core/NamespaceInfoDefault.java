/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.HashMap;
import java.util.Map;

import nu.xom.XPathContext;

import org.specrunner.source.namespace.INamespaceInfo;

/**
 * Default namespace info extractor.
 * 
 * @author Thiago Santos.
 * 
 */
public class NamespaceInfoDefault implements INamespaceInfo {

    /**
     * Map from prefix to URI.
     */
    private Map<String, String> prefixToUri = new HashMap<String, String>();
    /**
     * Map from URI to prefix.
     */
    private Map<String, String> uriToPrefix = new HashMap<String, String>();
    /**
     * Map from URI to XPathContext.
     */
    private Map<String, XPathContext> uriToContext = new HashMap<String, XPathContext>();
    /**
     * Map from prefix to XPathContext.
     */
    private Map<String, XPathContext> prefixToContext = new HashMap<String, XPathContext>();

    /**
     * Map of namespace information.
     * 
     * @param prefixToUri
     *            Map from prefix to uri.
     * @param uriToPrefix
     *            Map from uri to prefix.
     * @param prefixToContext
     *            Map from prefix to context.
     * @param uriToContext
     *            Map from uri to context.
     */
    public NamespaceInfoDefault(Map<String, String> prefixToUri, Map<String, String> uriToPrefix, Map<String, XPathContext> prefixToContext, Map<String, XPathContext> uriToContext) {
        this.prefixToUri = prefixToUri;
        this.uriToPrefix = uriToPrefix;
        this.prefixToContext = prefixToContext;
        this.uriToContext = uriToContext;
    }

    @Override
    public String getPrefix(String uri) {
        return uriToPrefix.get(uri);
    }

    @Override
    public String getURI(String prefix) {
        return prefixToUri.get(prefix);
    }

    @Override
    public XPathContext getByPrefix(String prefix) {
        return prefixToContext.get(prefix);
    }

    @Override
    public XPathContext getByURI(String uri) {
        return uriToContext.get(uri);
    }
}
