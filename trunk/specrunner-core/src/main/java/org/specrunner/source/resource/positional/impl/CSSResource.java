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
package org.specrunner.source.resource.positional.impl;

import java.io.File;

import nu.xom.Attribute;
import nu.xom.Element;

import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.positional.Position;

/**
 * A CSS resource.
 * 
 * @author Thiago Santos
 * 
 */
public class CSSResource extends AbstractResourceHeader {

    /**
     * Creates CSS resource.
     * 
     * @param parent
     *            The source parent.
     * @param path
     *            The resource path.
     * @param classpath
     *            The classpath flag.
     * @param type
     *            The resource nature.
     * @param position
     *            The position.
     */
    public CSSResource(ISource parent, String path, boolean classpath, EType type, Position position) {
        super(parent, path, classpath, type, position);
    }

    @Override
    protected Element getHeaderTag() {
        Element result = new Element("style");
        result.addAttribute(new Attribute("type", "text/css"));
        return result;
    }

    @Override
    protected Element getHeaderTag(ISource output, String name) {
        Element result = new Element("link");
        result.addAttribute(new Attribute("href", localName(output, name)));
        result.addAttribute(new Attribute("rel", "stylesheet"));
        result.addAttribute(new Attribute("type", "text/css"));
        return result;
    }

    @Override
    protected File getFile(ISource output, String name) {
        return new File(output.getFile().getParentFile(), localName(output, name));
    }

    /**
     * Gets the local disk file name.
     * 
     * @param output
     *            The output source.
     * @param name
     *            The name.
     * @return The name of local resource.
     */
    protected String localName(ISource output, String name) {
        return output.getFile().getName() + "_res/" + name + ".css";
    }
}