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

import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.sql.input.INode;
import org.specrunner.sql.input.IRow;
import org.specrunner.sql.input.ITable;

/**
 * Default implementation using XOM.
 * 
 * @author Thiago Santos
 * 
 */
public class TableXOM extends NodeXOM implements ITable {

    /**
     * Default constructor.
     * 
     * @param element
     *            The element.
     */
    public TableXOM(Element element) {
        super(element);
    }

    @Override
    public INode caption() {
        return new NodeXOM(element.getFirstChildElement("caption"));
    }

    @Override
    public List<INode> headers() {
        return toNodeList("th");
    }

    @Override
    public List<IRow> rows() {
        List<IRow> result = new LinkedList<IRow>();
        Nodes es = element.query("descendant::tbody/tr");
        for (int i = 0; i < es.size(); i++) {
            result.add(new RowXOM((Element) es.get(i)));
        }
        return result;
    }
}