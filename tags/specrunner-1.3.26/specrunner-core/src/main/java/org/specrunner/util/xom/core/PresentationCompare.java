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
package org.specrunner.util.xom.core;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.xom.IPresentation;

/**
 * Presents two objects with their types.
 * 
 * @author Thiago Santos
 * 
 */
public class PresentationCompare implements IPresentation {

    /**
     * Expected object.
     */
    private Object expected;
    /**
     * Received object.
     */
    private Object received;

    /**
     * Create a detail comparison object.
     * 
     * @param expected
     *            The expected object.
     * @param received
     *            The received object.
     */
    public PresentationCompare(Object expected, Object received) {
        this.expected = expected;
        this.received = received;
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        String expStr = String.valueOf(expected);
        String expType = expected != null ? expected.getClass().getName() : "null";
        String recStr = String.valueOf(received);
        String recType = received != null ? received.getClass().getName() : "null";
        int sizeStr = (int) Math.max(expStr.length(), recStr.length());
        int sizeType = (int) Math.max(expType.length(), recType.length());
        sb.append(String.format("(expected): '%" + sizeStr + "s' (%" + sizeType + "s)", expStr, expType));
        sb.append('\n');
        sb.append(String.format("(received): '%" + sizeStr + "s' (%" + sizeType + "s)", recStr, recType));
        return sb.toString();
    }

    @Override
    public Node asNode() {
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "compare"));
        Element tr = new Element("tr");
        table.appendChild(tr);
        {
            Element td = new Element("td");
            td.addAttribute(new Attribute("class", "expected"));
            tr.appendChild(td);
            td.appendChild("(expected):");

            td = new Element("td");
            tr.appendChild(td);
            td.appendChild(String.valueOf(expected));

            td = new Element("td");
            tr.appendChild(td);
            String expType = expected != null ? expected.getClass().getName() : "null";
            td.appendChild(expType);
        }

        tr = new Element("tr");
        table.appendChild(tr);
        {
            Element td = new Element("td");
            td.addAttribute(new Attribute("class", "received"));
            tr.appendChild(td);
            td.appendChild("(received):");

            td = new Element("td");
            tr.appendChild(td);
            td.appendChild(String.valueOf(received));

            td = new Element("td");
            tr.appendChild(td);
            String expType = received != null ? received.getClass().getName() : "null";
            td.appendChild(expType);
        }
        return table;
    }
}