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
package org.specrunner.util.xom;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;


/**
 * Node utility class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilNode {

    /**
     * Any node in specification with attribute "ignore=true" will not be
     * evaluated. Ignore is also used in plugin specific features such as to
     * avoid cell comparison inside tables in WebDriver profile.
     */
    public static final String IGNORE = "ignore";
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
     * Check is a node is marked to be ignored.
     * 
     * @param node
     *            The node to be checked.
     * @return true, if node can be ignored, false, otherwise.
     */
    public static boolean isIgnore(Node node) {
        return node instanceof Element && ((Element) node).getAttribute(IGNORE) != null;
    }

    /**
     * Adds the information that a given node might be ignored by runners.
     * 
     * @param node
     *            The node to be ignored.
     */
    public static void setIgnore(Node node) {
        if (node instanceof Element) {
            ((Element) node).addAttribute(new Attribute(IGNORE, "true"));
        }
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

    /**
     * Get the highest node in the specification hierarchy.
     * 
     * @param nodes
     *            List of nodes.
     * @return The highest node.
     */
    public static Node getHighest(Nodes nodes) {
        Node result = null;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            int t = getDepth(n);
            if (t < min) {
                result = n;
                min = t;
            }
        }
        return result;
    }

    /**
     * Get the node depth in the specification.
     * 
     * @param node
     *            The node.
     * @return The height.
     */
    public static int getDepth(Node node) {
        int i = 0;
        Node parent = node.getParent();
        while (parent != null) {
            parent = parent.getParent();
            i++;
        }
        return i;
    }

    /**
     * Check if node matchs the XPath.
     * 
     * @param node
     *            The node.
     * @param xpath
     *            The xpath.
     * @return true, if matched, false, otherwise.
     */
    public static boolean matches(Node node, String xpath) {
        return node.getDocument().query(xpath).contains(node);
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
}