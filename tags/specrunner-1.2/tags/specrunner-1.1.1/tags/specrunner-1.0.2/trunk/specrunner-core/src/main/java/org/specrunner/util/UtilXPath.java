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

import nu.xom.Node;
import nu.xom.Nodes;

/**
 * XPath utility class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilXPath {

    /**
     * Hidden constructor.
     */
    private UtilXPath() {
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
}
