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

import java.io.File;
import java.net.URL;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;
import org.specrunner.util.resources.ResourceFinder;

/**
 * A resource.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractResourceElementAtt extends AbstractResourceElement {

    /**
     * The attribute where resource lies.
     */
    protected String attribute;

    /**
     * The log name.
     */
    protected String prefix;

    /**
     * Creates a resource.
     * 
     * @param parent
     *            The source parent.
     * @param path
     *            The resource path.
     * @param classpath
     *            The classpath flag.
     * @param type
     *            The resource nature.
     * @param element
     *            The referred element.
     * @param reference
     *            The reference attribute.
     * @param prefix
     *            The name to log.
     */
    public AbstractResourceElementAtt(ISource parent, String path, boolean classpath, EType type, Element element, String reference, String prefix) {
        super(parent, path, classpath, type, element);
        this.attribute = reference;
        this.prefix = prefix;
    }

    @Override
    public ISource writeTo(ISource target) throws ResourceException {
        try {
            File fout = null;
            URL url = null;
            if (isClasspath()) {
                url = SRServices.get(ResourceFinder.class).initilize().getSpecific(getResourcePath());
                File f = new File(getResourcePath());
                fout = new File(target.getFile().getParentFile(), target.getFile().getName() + "_res/" + f.getName());
            } else {
                url = new URL(getResourcePath());
                String srcChanged = getElement().getAttribute(getReferenceName()).getValue();
                fout = new File(target.getFile().getParentFile(), srcChanged);
            }
            UtilIO.writeToClose(url, fout);
            addNode(getElement());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        return target;
    }

    /**
     * Get the attribute name reference.
     * 
     * @return The reference. i.e. href, src, etc.
     */
    protected String getReferenceName() {
        return attribute;
    }

    @Override
    public String asString() {
        return getPrefix() + "(" + getResourcePath() + ")";
    }

    /**
     * Get a description prefix.
     * 
     * @return The prefix.
     */
    protected String getPrefix() {
        return prefix;
    }

    @Override
    public Node asNode() {
        Element e = (Element) getElement().copy();
        e.addAttribute(new Attribute(getReferenceName(), getResourcePath()));
        appendChild(e);
        return e;
    }

    /**
     * Append child elements, by default add a comment.
     * 
     * @param root
     *            The root element.
     */
    protected void appendChild(Element root) {
        root.appendChild(new Comment(" comment "));
    }
}