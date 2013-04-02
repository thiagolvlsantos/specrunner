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
package org.specrunner.sql.input.impl;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.sql.input.INode;

/**
 * Default implementation using XOM.
 * 
 * @author Thiago Santos
 * 
 */
public class NodeXOM implements INode {
    /**
     * The corresponding node element.
     */
    protected Element element;

    /**
     * Default constructor.
     * 
     * @param element
     *            A element.
     */
    public NodeXOM(Element element) {
        this.element = element;
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttributeValue(name);
    }

    @Override
    public INode setAttribute(String name, String value) {
        element.addAttribute(new Attribute(name, value));
        return this;
    }

    @Override
    public String getText() {
        return element.getValue();
    }

    /**
     * Convert to a node list.
     * 
     * @param name
     *            The node name to be used in "descendant::&lt;name&gt;".
     * @return A list of nodes.
     */
    protected List<INode> toNodeList(String name) {
        List<INode> result = new LinkedList<INode>();
        Nodes es = element.query("descendant::" + name);
        for (int i = 0; i < es.size(); i++) {
            result.add(new NodeXOM((Element) es.get(i)));
        }
        return result;
    }
}