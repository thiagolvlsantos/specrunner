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
package org.specrunner.sql.report;

import nu.xom.Node;

import org.specrunner.util.xom.IPresentation;

/**
 * Exception for schema reports.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ReportException extends Exception implements IPresentation {

    /**
     * Exception report.
     */
    private SchemaReport report;

    /**
     * Constructor.
     * 
     * @param report
     *            The report.
     */
    public ReportException(SchemaReport report) {
        this.report = report;
    }

    @Override
    public String getMessage() {
        return asString();
    }

    @Override
    public String asString() {
        return report.asString();
    }

    @Override
    public Node asNode() {
        return report.asNode();
    }
}
