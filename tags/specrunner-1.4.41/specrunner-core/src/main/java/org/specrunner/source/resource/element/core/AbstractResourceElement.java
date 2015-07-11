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
package org.specrunner.source.resource.element.core;

import nu.xom.Element;

import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.core.AbstractResource;
import org.specrunner.source.resource.element.IResourceElement;

/**
 * Abstract implementation of a resource with a Element counterpart.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractResourceElement extends AbstractResource implements IResourceElement {
    /**
     * The related element.
     */
    private Element element;

    /**
     * Creates a element resource.
     * 
     * @param parent
     *            The parent source.
     * @param path
     *            The resource path.
     * @param classpath
     *            The classpath flag.
     * @param type
     *            The resource nature.
     * @param element
     *            The referred element.
     */
    protected AbstractResourceElement(ISource parent, String path, boolean classpath, EType type, Element element) {
        super(parent, path, classpath, type);
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
    }
}