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
package org.specrunner.source.resource.impl;

import java.util.LinkedList;

import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.IResource;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.source.resource.positional.Position;
import org.specrunner.source.resource.positional.impl.CSSResource;
import org.specrunner.source.resource.positional.impl.JSResource;
import org.specrunner.util.UtilLog;

/**
 * Default resource manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ResourceManagerImpl extends LinkedList<IResource> implements IResourceManager {

    /**
     * The parent source.
     */
    private ISource parent;

    /**
     * Creates a resource manager to a source.
     * 
     * @param parent
     *            The parent.
     */
    public ResourceManagerImpl(ISource parent) {
        this.parent = parent;
    }

    @Override
    public ISource getParent() {
        return parent;
    }

    /**
     * Set resource manager parent.
     * 
     * @param parent
     *            The parent.
     */
    public void setParent(ISource parent) {
        this.parent = parent;
    }

    @Override
    public void addDefaultCss() throws ResourceException {
        addCss("css/specrunner.css", true, EType.BINARY, Position.HEAD_START);
        if (UtilLog.LOG.isDebugEnabled()) {
            addCss("css/specrunner_debug.css", true, EType.BINARY, Position.HEAD_START);
        }
    }

    @Override
    public void addDefaultJs() throws ResourceException {
        addJs("js/jquery.js", true, EType.BINARY, Position.HEAD_END);
        addJs("js/specrunner.js", true, EType.BINARY, Position.HEAD_END);
    }

    @Override
    public boolean add(IResource resource) {
        boolean result = false;
        if (!contains(resource)) {
            result = super.add(resource);
            resource.setParent(getParent());
        }
        return result;
    }

    @Override
    public IResource addCss(String path, boolean classpath, EType ref) throws ResourceException {
        return check(new CSSResource(parent, path, classpath, ref, Position.HEAD_END));
    }

    @Override
    public IResource addCss(String path, boolean classpath, EType ref, Position position) throws ResourceException {
        return check(new CSSResource(parent, path, classpath, ref, position));
    }

    /**
     * Add a resource.
     * 
     * @param result
     *            The resource.
     * @return The resource itself, if added, false, otherwise.
     */
    private IResource check(IResource result) {
        if (add(result)) {
            return result;
        }
        return null;
    }

    @Override
    public IResource addJs(String path, boolean classpath, EType ref) throws ResourceException {
        return check(new JSResource(parent, path, classpath, ref, Position.HEAD_END));
    }

    @Override
    public IResource addJs(String path, boolean classpath, EType ref, Position position) throws ResourceException {
        return check(new JSResource(parent, path, classpath, ref, position));
    }

    @Override
    public void merge(IResourceManager resources) {
        for (IResource r : resources) {
            add(r);
        }
    }
}