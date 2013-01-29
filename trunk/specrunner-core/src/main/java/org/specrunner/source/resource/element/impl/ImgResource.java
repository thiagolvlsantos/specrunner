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
package org.specrunner.source.resource.element.impl;

import java.io.File;
import java.net.URL;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilResources;

/**
 * A image resource.
 * 
 * @author Thiago Santos
 * 
 */
public class ImgResource extends AbstractResourceElement {

    /**
     * Creates an image resource.
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
     */
    public ImgResource(ISource parent, String path, boolean classpath, EType type, Element element) {
        super(parent, path, classpath, type, element);
    }

    @Override
    public ISource writeTo(ISource target) throws ResourceException {
        try {
            File fout = null;
            URL url = null;
            if (isClasspath()) {
                url = UtilResources.getMostSpecific(getResourcePath());
                File f = new File(getResourcePath());
                fout = new File(target.getFile().getParentFile(), target.getFile().getName() + "_res/" + f.getName());
            } else {
                url = new URL(getResourcePath());
                String srcChanged = getElement().getAttribute("src").getValue();
                fout = new File(target.getFile().getParentFile(), srcChanged);
            }
            UtilIO.writeToClose(url, fout);
        } catch (Exception e) {
            throw new ResourceException(e);
        }
        return null;
    }

    @Override
    public String asString() {
        return "IMG(" + getResourcePath() + ")";
    }

    @Override
    public Node asNode() {
        Element e = new Element("img");
        e.addAttribute(new Attribute("src", getResourcePath()));
        return e;
    }
}