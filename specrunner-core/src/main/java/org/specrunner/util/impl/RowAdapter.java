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
package org.specrunner.util.impl;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

/**
 * A row abstraction.
 * 
 * @author Thiago Santos
 * 
 */
public class RowAdapter extends ElementHolderImpl {

    /**
     * List of row cells.
     */
    private List<CellAdapter> cells;

    /**
     * The row element.
     * 
     * @param node
     *            The row.
     */
    public RowAdapter(Element node) {
        super(node);
    }

    /**
     * The row XPath.
     * 
     * @return The rows XPath.
     */
    public String getXPath() {
        return "child::td | child::th";
    }

    /**
     * Number of cells per row.
     * 
     * @return Number of cells.
     */
    public int getCellsCount() {
        return getCells().size();
    }

    /**
     * List of cells.
     * 
     * @return The list of cells.
     */
    public List<CellAdapter> getCells() {
        if (cells == null) {
            List<CellAdapter> result = new LinkedList<CellAdapter>();
            Nodes nodes = getElement().query(getXPath());
            for (int i = 0; i < nodes.size(); i++) {
                result.add(new CellAdapter((Element) nodes.get(i)));
            }
            cells = result;
        }
        return cells;
    }

    /**
     * Get cell at a given index.
     * 
     * @param i
     *            The index.
     * @return A cell adapter.
     */
    public CellAdapter getCell(int i) {
        return getCells().get(i);
    }
}