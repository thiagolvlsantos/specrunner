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
package org.specrunner.core.pipes.shutdown;

import org.specrunner.SRServices;
import org.specrunner.SRServices.ShutDown;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;

/**
 * Call reporter resume.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeResume implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws AbortException, PipelineException {
        SRServices services = ShutDown.recover(channel);
        IReporterFactory r = services.lookup(IReporterFactory.class);
        IReporter reporter = r.newReporter();
        reporter.report(services);
        return channel;
    }
}
