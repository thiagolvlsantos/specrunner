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
package org.specrunner.util;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.source.resource.positional.IResourcePositional;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

/**
 * Resources utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilResources {

    /**
     * Hidden constructor.
     */
    private UtilResources() {
    }

    /**
     * Add a positional resource to a document.
     * 
     * @param source
     *            The source.
     * @param resourcePositional
     *            The positional resource.
     * @throws ResourceException
     *             The adding errors.
     */
    public static void addToDoc(ISource source, IResourcePositional resourcePositional) throws ResourceException {
        try {
            Document d = source.getDocument();
            Nodes ns = d.query(resourcePositional.getPosition().getXpath());
            if (ns.size() > 0) {
                Node n = ns.get(0);
                ParentNode p = n.getParent();
                Node a = resourcePositional.asNode();
                switch (resourcePositional.getPosition().getPlace()) {
                case BEFORE:
                    p.insertChild(a, p.indexOf(n));
                    break;
                case AFTER:
                    p.insertChild(a, p.indexOf(n) + 1);
                    break;
                case START:
                    if (n instanceof ParentNode) {
                        ((ParentNode) n).insertChild(a, 0);
                    }
                    break;
                case END:
                    if (n instanceof ParentNode) {
                        ((ParentNode) n).appendChild(a);
                    }
                    break;
                default:
                }
            }
        } catch (SourceException e) {
            throw new ResourceException(e);
        }
    }
}
