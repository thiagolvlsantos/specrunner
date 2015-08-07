/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.report.core;

import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;
import org.specrunner.report.IReporterFactoryGroup;
import org.specrunner.report.IReporterGroup;
import org.specrunner.util.composite.core.CompositeImpl;

/**
 * Default reporter factory group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterFactoryGroupImpl extends CompositeImpl<IReporterFactoryGroup, IReporterFactory> implements IReporterFactory {

    @Override
    public IReporter newReporter() {
        IReporterGroup result = new ReporterGroupImpl();
        for (IReporterFactory irf : getChildren()) {
            result.add(irf.newReporter());
        }
        return result;
    }

}
