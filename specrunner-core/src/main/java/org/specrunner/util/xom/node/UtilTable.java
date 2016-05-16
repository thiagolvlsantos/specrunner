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
package org.specrunner.util.xom.node;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.util.xom.UtilNode;

/**
 * Table utility class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilTable {
    /**
     * Hidden constructor.
     */
    private UtilTable() {
    }

    /**
     * Check if a node is a table.
     * 
     * @param node
     *            The node.
     * @return true, if is a table, false, otherwise.
     */
    public static boolean isTable(Node node) {
        return UtilNode.isElement(node, "table");
    }

    /**
     * Creates a table adapter for the given node.
     * 
     * @param node
     *            The node.
     * @return The adapter.
     */
    public static TableAdapter newTable(Node node) {
        if (!UtilTable.isTable(node)) {
            throw new IllegalArgumentException("Node is not a table.");
        }
        return SRServices.get(ITableFactory.class).newTable((Element) node);
    }
}
