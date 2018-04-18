/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.sql.report;

import org.specrunner.util.exceptions.UnstackedException;
import org.specrunner.util.xom.IPresentation;

import nu.xom.Element;
import nu.xom.Node;

/**
 * Exception for schema reports.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ReportException extends Exception implements IPresentation, UnstackedException {

    /**
     * Cache of string representation.
     */
    private String txt;
    /**
     * Cache of node representation.
     */
    private Node xml;

    /**
     * Constructor.
     * 
     * @param report
     *            The report.
     */
    public ReportException(SchemaReport report) {
        txt = report.asString();
        Element error = new Element("span");
        error.appendChild("System database errors:\n");
        error.appendChild(report.asNode());
        xml = error;
    }

    @Override
    public String getMessage() {
        return asString();
    }

    @Override
    public String asString() {
        return "System database errors:\n" + txt;
    }

    @Override
    public Node asNode() {
        return xml;
    }
}
