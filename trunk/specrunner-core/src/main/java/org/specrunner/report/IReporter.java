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
package org.specrunner.report;

import java.util.Map;

import org.specrunner.result.IResultSet;

/**
 * A reporter. The reporter should extract information to final dump.
 * 
 * @author Thiago Santos
 * 
 */
public interface IReporter {

    /**
     * Add information to the reporter.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     */
    void analyse(IResultSet result, Map<String, Object> model);

    /**
     * Dump report information.
     */
    void report();
}