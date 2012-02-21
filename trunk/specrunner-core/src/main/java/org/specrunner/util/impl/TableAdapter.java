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
 * A table adapter to encapsulate XOM API.
 * 
 * @author Thiago Santos
 * 
 */
public class TableAdapter extends ElementHolderImpl {

    private List<CellAdapter> captions;
    private List<RowAdapter> rows;

    public TableAdapter(Element node) {
        super(node);
    }

    public String getXPath() {
        return "child::tr | child::thead/tr | child::tbody/tr";
    }

    public List<CellAdapter> getCaptions() {
        if (captions == null) {
            List<CellAdapter> result = new LinkedList<CellAdapter>();
            Nodes nodes = getElement().query("child::caption");
            for (int i = 0; i < nodes.size(); i++) {
                result.add(new CellAdapter((Element) nodes.get(i)));
            }
            captions = result;
        }
        return captions;
    }

    public int getRowCount() {
        return getRows().size();
    }

    public List<RowAdapter> getRows() {
        if (rows == null) {
            List<RowAdapter> result = new LinkedList<RowAdapter>();
            Nodes nodes = getElement().query(getXPath());
            for (int i = 0; i < nodes.size(); i++) {
                result.add(new RowAdapter((Element) nodes.get(i)));
            }
            rows = result;
        }
        return rows;
    }

    public RowAdapter getRow(int i) {
        return getRows().get(i);
    }
}
