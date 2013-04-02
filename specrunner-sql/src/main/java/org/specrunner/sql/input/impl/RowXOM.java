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

import java.util.List;

import nu.xom.Element;

import org.specrunner.sql.input.INode;
import org.specrunner.sql.input.IRow;

/**
 * Default implementation using XOM.
 * 
 * @author Thiago Santos
 * 
 */
public class RowXOM extends NodeXOM implements IRow {

    /**
     * Default constructor.
     * 
     * @param element
     *            The element.
     */
    public RowXOM(Element element) {
        super(element);
    }

    @Override
    public List<INode> cells() {
        return toNodeList("td");
    }
}