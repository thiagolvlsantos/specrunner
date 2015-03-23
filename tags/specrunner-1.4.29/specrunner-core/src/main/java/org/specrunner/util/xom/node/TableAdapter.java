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
package org.specrunner.util.xom.node;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.util.xom.node.core.NodeHolderDefault;

import nu.xom.Element;
import nu.xom.Nodes;

/**
 * A table adapter to encapsulate XOM API.
 * 
 * @author Thiago Santos
 * 
 */
public class TableAdapter extends NodeHolderDefault {

    /**
     * List of col elements.
     */
    protected List<CellAdapter> cols;
    /**
     * List of caption elements.
     */
    protected List<CellAdapter> captions;
    /**
     * The list of rows.
     */
    protected List<RowAdapter> rows;

    /**
     * The table element.
     * 
     * @param node
     *            The table.
     */
    public TableAdapter(Element node) {
        super(node);
    }

    /**
     * The counter of cols.
     * 
     * @return The number of cols.
     */
    public int getColsCount() {
        return getCols().size();
    }

    /**
     * Get cols list.
     * 
     * @return The cols list.
     */
    public List<CellAdapter> getCols() {
        if (cols == null) {
            List<CellAdapter> result = new LinkedList<CellAdapter>();
            Nodes nodes = getNode().query(getColsXPath());
            for (int i = 0; i < nodes.size(); i++) {
                result.add(new CellAdapter((Element) nodes.get(i)));
            }
            cols = result;
        }
        return cols;
    }

    /**
     * Gets the XPath for cols.
     * 
     * @return The cols XPath.
     */
    public String getColsXPath() {
        return "child::colgroup/col";
    }

    /**
     * The col given by that index.
     * 
     * @param i
     *            The col index.
     * @return The col.
     */
    public CellAdapter getCol(int i) {
        return getCols().get(i);
    }

    /**
     * The counter of captions.
     * 
     * @return The number of captions.
     */
    public int getCaptionsCount() {
        return getCaptions().size();
    }

    /**
     * Get captions list.
     * 
     * @return The captions list.
     */
    public List<CellAdapter> getCaptions() {
        if (captions == null) {
            List<CellAdapter> result = new LinkedList<CellAdapter>();
            Nodes nodes = getNode().query(getCaptionsXPath());
            for (int i = 0; i < nodes.size(); i++) {
                result.add(new CellAdapter((Element) nodes.get(i)));
            }
            captions = result;
        }
        return captions;
    }

    /**
     * Gets the XPath for captions.
     * 
     * @return The captions XPath.
     */
    public String getCaptionsXPath() {
        return "child::caption";
    }

    /**
     * The caption given by that index.
     * 
     * @param i
     *            The caption index.
     * @return The caption.
     */
    public CellAdapter getCaption(int i) {
        return getCaptions().get(i);
    }

    /**
     * The counter of rows.
     * 
     * @return The number of rows.
     */
    public int getRowCount() {
        return getRows().size();
    }

    /**
     * The rows list.
     * 
     * @return The rows list.
     */
    public List<RowAdapter> getRows() {
        if (rows == null) {
            List<RowAdapter> result = new LinkedList<RowAdapter>();
            Nodes nodes = getNode().query(getRowsXPath());
            for (int i = 0; i < nodes.size(); i++) {
                result.add(new RowAdapter((Element) nodes.get(i)));
            }
            rows = result;
        }
        return rows;
    }

    /**
     * Gets the XPath for rows.
     * 
     * @return The rows XPath.
     */
    public String getRowsXPath() {
        return "child::tr | child::thead/tr | child::tbody/tr";
    }

    /**
     * The row given by that index.
     * 
     * @param i
     *            The row index.
     * @return The row.
     */
    public RowAdapter getRow(int i) {
        return getRows().get(i);
    }

    /**
     * Filter a table in range of columns.
     * 
     * @param fixedCols
     *            Fixed columns.
     * @param colIndex
     *            Col index.
     * @param columnIndex
     *            Columns index.
     * @param span
     *            Colspan.
     */
    public void select(int fixedCols, int colIndex, int columnIndex, int span) {
        int i = 0;
        for (CellAdapter ca : getCols()) {
            if (i > 0 && i != colIndex) {
                ca.detach();
            }
            i++;
        }
        for (RowAdapter ra : getRows()) {
            i = 0;
            for (CellAdapter ca : ra.getCells()) {
                if (i > fixedCols && (i < columnIndex || i >= columnIndex + span)) {
                    ca.detach();
                }
                i++;
            }
        }
    }
}
