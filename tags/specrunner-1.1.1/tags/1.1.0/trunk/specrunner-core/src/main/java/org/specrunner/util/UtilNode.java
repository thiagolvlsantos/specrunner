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
package org.specrunner.util;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.impl.TableAdapter;

/**
 * Node utility class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilNode {

    /**
     * The CSS attribute name.
     */
    public static final String ATT_CSS = "class";

    /**
     * Hidden constructor.
     */
    private UtilNode() {
    }

    /**
     * Creates a table adapter for the given node.
     * 
     * @param ele
     *            The element.
     * @return The adapter.
     */
    public static TableAdapter newTableAdapter(Element ele) {
        return new TableAdapter(ele);
    }

    /**
     * Add a CSS to the node.
     * 
     * @param node
     *            The node.
     * @param css
     *            The css to be added (appended).
     * @return The node.
     */
    public static Node appendCss(Node node, String css) {
        if (node instanceof Element) {
            Element ele = (Element) node;
            Attribute att = ele.getAttribute(ATT_CSS);
            if (att == null) {
                ele.addAttribute(new Attribute(ATT_CSS, css));
            } else {
                String value = att.getValue();
                if (!value.contains(css)) {
                    att.setValue(value + " " + css);
                }
            }
        }
        return node;
    }
}