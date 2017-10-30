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
package org.specrunner.source.resource.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.IResource;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.resources.ResourceFinder;

import nu.xom.Node;

/**
 * Partial resource implementation.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractResource implements IResource {

    /**
     * The parent source.
     */
    private ISource parent;
    /**
     * The resource path.
     */
    private String resourcePath;
    /**
     * The classpath flag.
     */
    private boolean classpath;
    /**
     * The resource nature.
     */
    private EType type;
    /**
     * Resource reference after writeTo call.
     */
    private List<Node> nodes = new LinkedList<Node>();

    /**
     * Partial constructor.
     * 
     * @param parent
     *            The parent.
     * @param resourcePath
     *            The resource path.
     * @param classpath
     *            The classpath flag.
     * @param type
     *            The resource nature.
     */
    protected AbstractResource(ISource parent, String resourcePath, boolean classpath, EType type) {
        this.parent = parent;
        this.resourcePath = resourcePath;
        this.classpath = classpath;
        this.type = type;
    }

    @Override
    public ISource getParent() {
        return parent;
    }

    @Override
    public void setParent(ISource parent) {
        this.parent = parent;
    }

    @Override
    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public EType getType() {
        return type;
    }

    @Override
    public void setType(EType type) {
        this.type = type;
    }

    @Override
    public boolean isClasspath() {
        return classpath;
    }

    @Override
    public void setClasspath(boolean classpath) {
        this.classpath = classpath;
    }

    @Override
    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public void addNode(Node node) {
        this.nodes.add(node);
    }

    /**
     * Gets the list of URL resources.
     * 
     * @return List of resources.
     * @throws ResourceException
     *             On resource listing errors.
     */
    protected List<URL> getResourceURLs() throws ResourceException {
        List<URL> files;
        try {
            if (isClasspath()) {
                files = SRServices.get(ResourceFinder.class).initilize().getAllResources(getResourcePath());
                Collections.reverse(files);
            } else {
                files = Arrays.asList(new URL(getResourcePath()));
            }
        } catch (IOException e) {
            throw new ResourceException(e);
        }
        if (files == null || files.isEmpty()) {
            throw new ResourceException("Resource " + this + " not found.");
        }
        return filterExisting(files);
    }

    /**
     * Filter only available resources.
     * 
     * @param urls
     *            List of resource URLs.
     * @return The filtered version.
     * @throws ResourceException
     *             On file loca
     */
    protected List<URL> filterExisting(List<URL> urls) throws ResourceException {
        List<URL> existing = new LinkedList<URL>();
        for (URL url : urls) {
            if ("file".equals(url.getProtocol())) {
                File local = null;
                try {
                    local = new File(url.toURI());
                } catch (URISyntaxException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Invalid URI for file " + local + ".");
                    }
                }
                if (local != null && local.exists()) {
                    existing.add(url);
                } else {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Resource file " + url + " not found.");
                    }
                }
            } else {
                existing.add(url);
            }
        }
        return existing;
    }

}
