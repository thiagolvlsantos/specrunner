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
package org.specrunner.report.impl;

import java.util.Map;

import org.specrunner.SpecRunnerServices;
import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterGroup;
import org.specrunner.result.IResultSet;
import org.specrunner.util.composite.CompositeImpl;

/**
 * Default reporter group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterGroupImpl extends CompositeImpl<IReporterGroup, IReporter> implements IReporterGroup {

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        for (IReporter r : getChildren()) {
            r.analyse(result, model);
        }
    }

    @Override
    public String resume() {
        StringBuilder sb = new StringBuilder();
        for (IReporter r : getChildren()) {
            sb.append(r.resume());
        }
        return sb.toString();
    }

    @Override
    public void report(SpecRunnerServices services) {
        for (IReporter r : getChildren()) {
            r.report(services);
        }
    }
}